package pg.ui.window.controller.completable;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.scene.layout.Background;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.exceptions.ProgramException;
import pg.exceptions.UIError;
import pg.program.ApiName;
import pg.props.ApplicationPropertiesHelper;
import pg.util.AppConstants;
import pg.util.ImageUtils;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.DSAllowedProtocol;
import pg.web.ds.DSGeneralResponse;
import pg.web.ds.detail.DsApiDetail;

import java.io.IOException;
import java.util.Optional;

public class SearchCleanCompletable implements Runnable {
    private static final Logger logger  = LogManager.getLogger(SearchCleanCompletable.class);
    private final ApplicationPropertiesHelper application;
    private final DsApiDetail dsApiDetail;
    private final Property<Background> backgroundProperty;
    private String serverUrl;
    private String searchTaskId;

    public SearchCleanCompletable(DsApiDetail dsApiDetail, Property<Background> backgroundProperty, String searchTaskId) {
        this.backgroundProperty = backgroundProperty;
        this.searchTaskId = searchTaskId;
        this.application = ApplicationPropertiesHelper.getInstance();
        this.dsApiDetail = dsApiDetail;
    }

    @Override
    public void run() {
        btSearchClean();
        updateProgressImage();
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
                backgroundProperty.setValue(ImageUtils.getBackground(AppConstants.CHECK_GIF));
                logger.info("Image changed to completed.");
            } catch (IOException e) {
                logger.warn("Could not load progress gif.");
            }
        });
    }
}
