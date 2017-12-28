package pg.service.ds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.props.ApplicationPropertiesHelper;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.*;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.DSGeneralResponse;
import pg.web.response.DSLoginResponse;
import pg.web.response.DSResponse;
import pg.web.response.DSTaskListResponse;
import pg.web.response.detail.DSApiDetails;
import pg.web.synology.AuthMethod;
import pg.web.synology.DSTaskMethod;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-15*/
public class DiskStationServiceImpl implements DiskStationService {

    private static final Logger logger = LogManager.getLogger(DiskStationServiceImpl.class);

    private final ApplicationPropertiesHelper application;
    private DSApiDetails authInfo;
    private DSApiDetails downloadStationTask;
    private List<ReducedDetail> foundTorrents;
    private String sid;
    private String serverUrl;

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
            AllowedProtocol protocol = application.getServerPort(AllowedProtocol.https);
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
                        sid = loginResponse.getLoginDetails().getSid();
                        String logMsg = String.format("Login successful. sid = %s.", sid);
                        logger.info(logMsg);
                    } else {
                        String logMsg = String.format("Login unsuccessful. Details %d - %s.",
                                loginResponse.getError().getCode(),
                                DSError.getAuthError(loginResponse.getError().getCode()));
                        throw new IllegalArgumentException(logMsg);
                    }
                } else {
                    throw new IllegalArgumentException("Login unsuccessful. No response from server.");
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
                "method=" + AuthMethod.LOGIN.method() + "&" +
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
                logger.info("RestURL: ["+requestUrl+"].");

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
                                taskListResponse.getTaskListDetail().getTotal());
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
                "method=" + AuthMethod.LOGOUT.method() + "&" +
                "session=" + "DownloadStation" + "&" +
                "format=" + "sid";
    }

    @Override
    public void writeTorrentsOnDS() {
        String destination = application.getTorrentLocation("");
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

}
