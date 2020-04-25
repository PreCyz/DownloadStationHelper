package pg.ui.window.controller.completable;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.layout.Background;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.exceptions.ProgramException;
import pg.exceptions.UIError;
import pg.program.ApiName;
import pg.program.SearchItem;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.LoginCall;
import pg.util.AppConstants;
import pg.util.ImageUtils;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.DSAllowedProtocol;
import pg.web.ds.DSGeneralResponse;
import pg.web.ds.btsearch.DSSearchListData;
import pg.web.ds.btsearch.DSSearchListResponse;
import pg.web.ds.btsearch.DSSearchStartResponse;
import pg.web.ds.detail.DsApiDetail;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class SearchCompletable extends Task<String> {
    private static final Logger logger  = LogManager.getLogger(SearchCompletable.class);
    private final ApplicationPropertiesHelper application;
    private final Property<ObservableList<SearchItem>> listProperty;
    private final Property<Background> backgroundProperty;
    private final DsApiDetail dsApiDetail;
    private final String keywords;
    private final WindowHandler windowHandler;
    private String serverUrl;
    private String searchTaskId;

    public SearchCompletable(Property<ObservableList<SearchItem>> listProperty, Property<Background> backgroundProperty,
            String keywords, DsApiDetail dsApiDetail, WindowHandler windowHandler) {
        this.listProperty = listProperty;
        this.backgroundProperty = backgroundProperty;
        this.keywords = keywords;
        this.windowHandler = windowHandler;
        this.application = ApplicationPropertiesHelper.getInstance();
        this.dsApiDetail = dsApiDetail;
    }

    @Override
    protected String call() throws Exception {
        updateSid();

        btSearchStart();

        final long THREAD_SLEEP_MILLISECONDS = 1000 * application.getLiveTrackInterval();
        final Set<SearchItem> dsSearchListItems = new LinkedHashSet<>();
        boolean isSearchFinished;
        do {
            logger.info("Waiting {} seconds to check if search is finished.", application.getLiveTrackInterval());
            Thread.sleep(THREAD_SLEEP_MILLISECONDS);
            final DSSearchListResponse listResponse = btSearchList();
            final DSSearchListData data = listResponse.getData();

            updateListProperty(dsSearchListItems, data);

            isSearchFinished = data.isFinished();

            logger.info("Search finished on the server: [{}].", isSearchFinished);
        } while (!isSearchFinished);

        btSearchClean();

        updateProgressImage();

        return searchTaskId;
    }

    private void updateListProperty(Set<SearchItem> dsSearchListItems, DSSearchListData data) {
        Platform.runLater(() -> {
            dsSearchListItems.addAll(data.getItems()
                    .stream()
                    .map(SearchItem::valueFrom)
                    .collect(toList())
            );
            final ObservableList<SearchItem> searchItems = FXCollections.observableArrayList(dsSearchListItems);
            listProperty.getValue().clear();
            listProperty.getValue().addAll(searchItems);
        });
    }

    private void updateSid() {
        if (dsApiDetail.getSid() == null) {
            LoginCall loginCall = new LoginCall(dsApiDetail.getAuthInfo());
            final String sid = loginCall.call();
            dsApiDetail.setSid(sid);
            windowHandler.setDsApiDetail(dsApiDetail);
        }
    }

    private void btSearchStart() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildBtSearchStartUrl(serverUrl, keywords);

            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                logger.info(response.get());
                Optional<DSSearchStartResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSSearchStartResponse.class);
                if (jsonResponse.isPresent()) {
                    searchTaskId = jsonResponse.get().getData().getTaskId();
                    windowHandler.setSearchTaskId(searchTaskId);
                    logger.info("Success. The search task id {}", searchTaskId);
                } else {
                    throw new ProgramException(UIError.SEARCH_START,
                            new IllegalArgumentException("Search start task with error. No details."));
                }
            }
        }
    }

    protected String prepareServerUrl() {
        if (serverUrl != null) {
            return serverUrl;
        }
        String server = application.getServerUrl();
        if (server != null && !server.isEmpty()) {
            DSAllowedProtocol protocol = application.getServerPort(DSAllowedProtocol.https);
            return serverUrl = String.format("%s://%s:%s", protocol.name(), server, protocol.port());
        }
        return serverUrl = "";
    }

    protected String buildBtSearchStartUrl(String serverUrl, String keywords) {
        return serverUrl + "/webapi/" + dsApiDetail.getDownloadStationBtSearch().getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + dsApiDetail.getDownloadStationBtSearch().getMaxVersion() + "&" +
                "method=start" + "&" +
                "keyword=" + URLEncoder.encode(keywords, StandardCharsets.UTF_8) + "&" +
                "module=enabled" + "&" +
                "_sid=" + dsApiDetail.getSid();
    }

    public DSSearchListResponse btSearchList() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildBtSearchListUrl(serverUrl);

            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                Optional<DSSearchListResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSSearchListResponse.class);

                if (jsonResponse.isPresent()) {
                    logger.info("Is search finished: {}", jsonResponse.get().getData().isFinished());
                    return jsonResponse.get();
                } else {
                    throw new ProgramException(UIError.SEARCH_LIST,
                            new IllegalArgumentException("Search list task with error. No details."));
                }
            }
        }
        throw new ProgramException(UIError.SEARCH_LIST,
                new IllegalArgumentException("Search list task with error. No details."));
    }

    /**
     * taskid - Task ID
     * offset - Optional. Beginning task on the requested record. Default to '0'.
     * limit - Optional. Number of records requested: '-1' means to list all tasks. Default to '-1'.
     * sort_by - Optional. Possible value is title, size, date, peers, provider, seeds or leechs. Default to 'title'
     * sort_direction - Possible value is desc or asc
     * filter_category - Optional. Filter the records by the category using Category ID returned by getCategory function. Default to ''
     * filter_title - Optional. Filter the records by the title using this parameter. Default to ''
     */
    protected String buildBtSearchListUrl(String serverUrl) {
        return serverUrl + "/webapi/" + dsApiDetail.getDownloadStationBtSearch().getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + dsApiDetail.getDownloadStationBtSearch().getMaxVersion() + "&" +
                "method=list" + "&" +
                "taskid=" + searchTaskId + "&" +
                "offset=0" + "&" +
                "limit=" + application.getSearchLimit() + "&" +
                "sort_by=seeds" + "&" +
                "filter_category=" + "&" +
                "filter_title=" + "&" +
                "sort_direction=DESC" + "&" +
                "_sid=" + dsApiDetail.getSid();
    }

    private void btSearchClean() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildBtSearchCleanUrl(serverUrl);

            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                logger.info(response.get());
                Optional<DSGeneralResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSGeneralResponse.class);

                if (jsonResponse.isPresent()) {
                    logger.info("Is search clean finished with success: {}", jsonResponse.get().isSuccess());
                } else {
                    throw new ProgramException(UIError.SEARCH_CLEAN,
                            new IllegalArgumentException("Search clean task with error. No details."));
                }
            } else {
                logger.info("Success - no response.");
            }
        }
    }

    protected String buildBtSearchCleanUrl(String serverUrl) {
        return serverUrl + "/webapi/" + dsApiDetail.getDownloadStationBtSearch().getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + dsApiDetail.getDownloadStationBtSearch().getMaxVersion() + "&" +
                "method=clean" + "&" +
                "taskid=" + searchTaskId + "&" +
                "_sid=" + dsApiDetail.getSid();
    }

    private void updateProgressImage() {
        Platform.runLater(() -> {
            try {
                backgroundProperty.setValue(ImageUtils.getBackground(AppConstants.CHECK_GIF, 3, 3));
                logger.info("Image changed to completed.");
            } catch (IOException e) {
                logger.warn("Could not load progress gif.");
            }
        });
    }
}
