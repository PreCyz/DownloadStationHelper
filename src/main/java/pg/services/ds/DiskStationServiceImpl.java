package pg.services.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.program.ApiName;
import pg.program.SettingKeys;
import pg.program.TorrentUrlType;
import pg.props.ApplicationPropertiesHelper;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.*;
import pg.web.ds.btsearch.DSSearchCategoriesResponse;
import pg.web.ds.btsearch.DSSearchListResponse;
import pg.web.ds.btsearch.DSSearchModulesResponse;
import pg.web.ds.btsearch.DSSearchStartResponse;
import pg.web.ds.detail.DSApiDetails;
import pg.web.torrent.ReducedDetail;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Gawa 2017-09-15
 */
public class DiskStationServiceImpl implements DiskStationService {

    private static final Logger logger = LoggerFactory.getLogger(DiskStationServiceImpl.class);

    private final ApplicationPropertiesHelper application;
    private DSApiDetails authInfo;
    private DSApiDetails downloadStationTask;
    private DSApiDetails downloadStationBtSearch;
    private List<ReducedDetail> foundTorrents;
    private String sid;
    private String serverUrl;
    private String searchTaskId;

    public DiskStationServiceImpl(List<ReducedDetail> foundTorrents) {
        this.application = ApplicationPropertiesHelper.getInstance();
        this.foundTorrents = foundTorrents;
    }

    @Override
    public void prepareAvailableOperations() {
        String requestUrl = prepareServerUrl();
        if (requestUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            requestUrl += application.getApiInfo();
            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                Optional<DSResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSResponse.class);
                if (jsonResponse.isPresent()) {
                    DSResponse DSResponse = jsonResponse.get();
                    if (DSResponse.isSuccess()) {
                        authInfo = DSResponse.getDsInfo().getAuthInfo();
                        downloadStationTask = DSResponse.getDsInfo().getDownloadStationTask();
                        downloadStationBtSearch = DSResponse.getDsInfo().getDownloadStationBtSearch();
                    }
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

    @Override
    public void loginToDiskStation() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildLoginUrl(serverUrl);
            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                Optional<DSLoginResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSLoginResponse.class);
                logger.info("Login format sid.");
                if (jsonResponse.isPresent()) {
                    DSLoginResponse loginResponse = jsonResponse.get();
                    if (loginResponse.isSuccess()) {
                        sid = loginResponse.getDSLoginDetails().getSid();
                        String logMsg = String.format("Login successful. sid = %s.", sid);
                        logger.info(logMsg);
                    } else {
                        String logMsg = String.format("Login unsuccessful. Details %d - %s.",
                                loginResponse.getError().getCode(),
                                DSError.getAuthError(loginResponse.getError().getCode()));
                        throw new IllegalArgumentException(logMsg);
                    }
                } else {
                    throw new IllegalArgumentException("Login unsuccessful. No ds from server.");
                }

            }
        }
    }

    protected String buildLoginUrl(String serverUrl) {
        String userName = application.getUsername();
        String password = application.getPassword();
        return serverUrl + "/webapi/" + authInfo.getPath() +
                "?" +
                "api=" + ApiName.API_AUTH + "&" +
                "version=" + authInfo.getMaxVersion() + "&" +
                "method=" + DSActivity.LOGIN.method() + "&" +
                "account=" + userName + "&" +
                "passwd=" + password + "&" +
                "session=" + "DownloadStation" + "&" +
                "format=" + "sid";
    }

    @Override
    public void createDownloadStationTasks() {
        if (!foundTorrents.isEmpty()) {
            String serverUrl = prepareServerUrl();
            if (serverUrl.isEmpty()) {
                logger.info("Server URL not specified.");
            } else {
                String requestUrl = buildCreateTaskUrl(serverUrl);
                logger.info("RestURL: [" + requestUrl + "].");

                GetClient client = new GetClient(requestUrl);
                Optional<String> response = client.get();
                if (response.isPresent()) {
                    Optional<DSGeneralResponse> jsonResponse = JsonUtils.convertFromString(response.get(), DSGeneralResponse.class);
                    if (jsonResponse.isPresent()) {
                        DSGeneralResponse createTaskResponse = jsonResponse.get();
                        if (createTaskResponse.isSuccess()) {
                            logger.info("Task creation successful.");
                        } else {
                            String logMsg = String.format("Task creation finished with error %d - %s.",
                                    createTaskResponse.getError().getCode(),
                                    DSError.getTaskError(createTaskResponse.getError().getCode()));
                            throw new IllegalArgumentException(logMsg);
                        }
                    } else {
                        throw new IllegalArgumentException("Task creation with error. No details.");
                    }
                }
            }
        } else {
            logger.info("No task to create.");
        }
    }

    protected String buildCreateTaskUrl(String serverUrl) {
        final TorrentUrlType urlType = TorrentUrlType.valueOf(
                application.getTorrentUrlType(TorrentUrlType.torrent.name())
        );
        Function<ReducedDetail, String> extractUrlType = reducedDetail -> {
            switch (urlType) {
                case magnet:
                    return reducedDetail.getMagnetUrl();
                default:
                    return reducedDetail.getTorrentUrl();
            }
        };
        String uri = String.join(",", foundTorrents.stream()
                .map(extractUrlType)
                .collect(Collectors.toList())
        );

        String destination = application.getDestination();

        return serverUrl + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + DSTaskMethod.CREATE.method() + "&" +
                "_sid=" + sid + "&" +
                "destination=" + destination + "&" +
                "uri=" + uri;
    }

    @Override
    public void listOfTasks() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildTaskListUrl(serverUrl);

            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                Optional<DSTaskListResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSTaskListResponse.class);
                if (jsonResponse.isPresent()) {
                    DSTaskListResponse taskListResponse = jsonResponse.get();
                    if (taskListResponse.isSuccess()) {
                        logger.info("Total number of tasks on download station is: {}.",
                                taskListResponse.getDSTaskListDetail().getTotal());
                    } else {
                        String logMsg = String.format("List of tasks finished with error %d - %s.",
                                taskListResponse.getError().getCode(),
                                DSError.getTaskError(taskListResponse.getError().getCode()));
                        throw new IllegalArgumentException(logMsg);

                    }
                } else {
                    throw new IllegalArgumentException("List of tasks with error. No details.");
                }
            }
        }
    }

    protected String buildTaskListUrl(String serverUrl) {
        return serverUrl + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + "list" + "&" +
                "_sid=" + sid;
    }

    @Override
    public void logoutFromDiskStation() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildLogoutUrl(serverUrl);
            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                Optional<DSGeneralResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSGeneralResponse.class);
                if (jsonResponse.isPresent()) {
                    DSGeneralResponse logoutResponse = jsonResponse.get();
                    if (logoutResponse.isSuccess()) {
                        logger.info("Logout finished.");
                    } else {
                        String logMsg = String.format("Logout with error %d - %s.",
                                logoutResponse.getError().getCode(),
                                DSError.getAuthError(logoutResponse.getError().getCode()));
                        throw new IllegalArgumentException(logMsg);
                    }
                } else {
                    throw new IllegalArgumentException("Logout with error. No details.");
                }
            }
        }
    }

    protected String buildLogoutUrl(String serverUrl) {
        return serverUrl + "/webapi/" + authInfo.getPath() +
                "?" +
                "api=" + ApiName.API_AUTH + "&" +
                "version=" + authInfo.getMaxVersion() + "&" +
                "method=" + DSActivity.LOGOUT.method() + "&" +
                "session=" + "DownloadStation" + "&" +
                "format=" + "sid";
    }

    @Override
    public void writeTorrentsOnDS() {
        String destination = application.getTorrentLocation();
        if (destination == null || destination.isEmpty()) {
            logger.info("{} not specified. Add it to application.properties.", SettingKeys.TORRENT_LOCATION.key());
            return;
        }
        if (foundTorrents.isEmpty()) {
            logger.info("No task to create.");
            return;
        }
        foundTorrents.forEach(torrent -> {
            String filePath = destination + torrent.getTitle() + ".torrent";
            new GetClient(torrent.getTorrentUrl()).downloadFile(filePath);
            logger.info("File [{}] saved in {}.", torrent.getTitle(), destination);
        });
    }

    @Override
    public void btSearchStart(String keywords) {
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
                    logger.info("Success. The search task id {}", searchTaskId);
                } else {
                    throw new IllegalArgumentException("List of tasks with error. No details.");
                }
            }
        }
    }

    protected String buildBtSearchStartUrl(String serverUrl, String keywords) {
        return serverUrl + "/webapi/" + downloadStationBtSearch.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + downloadStationBtSearch.getMaxVersion() + "&" +
                "method=start" + "&" +
                "keyword=" + URLEncoder.encode(keywords, StandardCharsets.UTF_8) + "&" +
                "module=enabled" + "&" +
                "_sid=" + sid;
    }

    @Override
    public boolean btSearchList() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildBtSearchListUrl(serverUrl);

            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                logger.info(response.get());
                Optional<DSSearchListResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSSearchListResponse.class);

                if (jsonResponse.isPresent()) {
                    logger.info("Is search finished: {}", jsonResponse.get().getData().isFinished());
                    return jsonResponse.get().getData().isFinished();
                } else {
                    throw new IllegalArgumentException("There is no response for the bt search.");
                }
            }
        }
        throw new IllegalArgumentException("Could not finish bt search.");
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
        return serverUrl + "/webapi/" + downloadStationBtSearch.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + downloadStationBtSearch.getMaxVersion() + "&" +
                "method=list" + "&" +
                "taskid=" + searchTaskId + "&" +
                "offset=0" + "&" +
                "limit=25" + "&" +
                "sort_by=seeds" + "&" +
                "filter_category=" + "&" +
                "filter_title=" + "&" +
                "sort_direction=DESC" + "&" +
                "_sid=" + sid;
    }

    @Override
    public void btSearchClean() {
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
                    throw new IllegalArgumentException("There is no response for search clean.");
                }
            } else {
                logger.info("Success - no response.");
            }
        }
    }

    protected String buildBtSearchCleanUrl(String serverUrl) {
        return serverUrl + "/webapi/" + downloadStationBtSearch.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + downloadStationBtSearch.getMaxVersion() + "&" +
                "method=clean" + "&" +
                "taskid=" + searchTaskId + "&" +
                "_sid=" + sid;
    }

    @Override
    public void btSearchCategories() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildBtSearchCategoriesUrl(serverUrl);

            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                logger.info(response.get());
                Optional<DSSearchCategoriesResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSSearchCategoriesResponse.class);

                if (jsonResponse.isPresent()) {
                    logger.info("Is search get categories finished with success: {}", jsonResponse.get().isSuccess());
                    logger.info("{}", jsonResponse.get());
                } else {
                    throw new IllegalArgumentException("There is no response for search categories.");
                }
            } else {
                logger.info("Success - no response.");
            }
        }
    }

    protected String buildBtSearchCategoriesUrl(String serverUrl) {
        return serverUrl + "/webapi/" + downloadStationBtSearch.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + downloadStationBtSearch.getMaxVersion() + "&" +
                "method=getCategory" + "&" +
                "_sid=" + sid;
    }

    @Override
    public void btSearchModules() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildBtSearchModulesUrl(serverUrl);

            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                logger.info(response.get());
                Optional<DSSearchModulesResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSSearchModulesResponse.class);

                if (jsonResponse.isPresent()) {
                    logger.info("Is search get modules finished with success: {}", jsonResponse.get().isSuccess());
                    logger.info("{}", jsonResponse.get());
                } else {
                    throw new IllegalArgumentException("There is no response for search modules.");
                }
            } else {
                logger.info("Success - no response.");
            }
        }
    }

    private String buildBtSearchModulesUrl(String serverUrl) {
        return serverUrl + "/webapi/" + downloadStationBtSearch.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + downloadStationBtSearch.getMaxVersion() + "&" +
                "method=getModule" + "&" +
                "_sid=" + sid;
    }

}
