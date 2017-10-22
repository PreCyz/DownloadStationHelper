package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.props.ApplicationPropertiesHelper;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.*;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.GeneralResponse;
import pg.web.response.LoginResponse;
import pg.web.response.SynologyResponse;
import pg.web.response.TaskListResponse;
import pg.web.synology.AuthMethod;
import pg.web.synology.DSTaskMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-15*/
public class DiskStationServiceImpl implements DiskStationService {

    private static final Logger logger = LogManager.getLogger(DiskStationServiceImpl.class);

    private final ApplicationPropertiesHelper application;
    private ApiDetails authInfo;
    private ApiDetails downloadStationTask;
    private List<ReducedDetail> foundTorrents;
    private String sid;
    private String serverUrl;

    protected static Map<Integer, String> authErrorMap = new HashMap<>();
    static {
        authErrorMap.put(100, "Unknown error");
        authErrorMap.put(101, "Invalid parameter");
        authErrorMap.put(102, "The requested API does not exist");
        authErrorMap.put(103, "The requested method does not exist");
        authErrorMap.put(104, "The requested version does not support the functionality");
        authErrorMap.put(105, "The logged in session does not have permission");
        authErrorMap.put(106, "Session timeout");
        authErrorMap.put(107, "Session interrupted by duplicate login");
        authErrorMap.put(400, "No such account or incorrect password");
        authErrorMap.put(401, "Account disabled");
        authErrorMap.put(402, "Permission denied");
        authErrorMap.put(403, "2-step verification code required");
        authErrorMap.put(404, "Failed to authenticate 2-step verification code");
    }

    protected static Map<Integer, String> taskErrorMap = new HashMap<>();
    static {
        taskErrorMap.put(100, "Unknown error");
        taskErrorMap.put(101, "Invalid parameter");
        taskErrorMap.put(102, "The requested API does not exist");
        taskErrorMap.put(103, "The requested method does not exist");
        taskErrorMap.put(104, "The requested version does not support the functionality");
        taskErrorMap.put(105, "The logged in session does not have permission");
        taskErrorMap.put(106, "Session timeout");
        taskErrorMap.put(107, "Session interrupted by duplicate login");
        taskErrorMap.put(400, "File upload failed");
        taskErrorMap.put(401, "Max number of tasks reached");
        taskErrorMap.put(402, "Destination denied");
        taskErrorMap.put(403, "Destination does not exist");
        taskErrorMap.put(404, "Invalid task id");
        taskErrorMap.put(405, "Invalid task action");
        taskErrorMap.put(406, "No default destination");
        taskErrorMap.put(407, "Set destination failed");
        taskErrorMap.put(408, "File does not exist");
    }

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
                Optional<SynologyResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), SynologyResponse.class);
                if (jsonResponse.isPresent()) {
                    SynologyResponse synologyResponse = jsonResponse.get();
                    if (synologyResponse.isSuccess()) {
                        authInfo = synologyResponse.getDetailResponse().getAuthInfo();
                        downloadStationTask = synologyResponse.getDetailResponse().getDownloadStationTask();
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
                Optional<LoginResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), LoginResponse.class);
                logger.info("Login format sid.");
                if (jsonResponse.isPresent()) {
                    LoginResponse loginResponse = jsonResponse.get();
                    if (loginResponse.isSuccess()) {
                        sid = loginResponse.getLoginDetails().getSid();
                        String logMsg = String.format("Login successful. sid = %s.", sid);
                        logger.info(logMsg);
                    } else {
                        String logMsg = String.format("Login unsuccessful. Details %d - %s.",
                                loginResponse.getError().getCode(),
                                authErrorMap.get(loginResponse.getError().getCode()));
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
                    Optional<GeneralResponse> jsonResponse = JsonUtils.convertFromString(response.get(), GeneralResponse.class);
                    if (jsonResponse.isPresent()) {
                        GeneralResponse createTaskResponse = jsonResponse.get();
                        if (createTaskResponse.isSuccess()) {
                            logger.info("Task creation successful.");
                        } else {
                            String logMsg = String.format("Task creation finished with error %d - %s.",
                                    createTaskResponse.getError().getCode(),
                                    taskErrorMap.get(createTaskResponse.getError().getCode()));
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
                Optional<TaskListResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), TaskListResponse.class);
                if (jsonResponse.isPresent()) {
                    TaskListResponse taskListResponse = jsonResponse.get();
                    if (taskListResponse.isSuccess()) {
                        logger.info("Total number of tasks on download station is: {}.",
                                taskListResponse.getTaskListDetail().getTotal());
                    } else {
                        String logMsg = String.format("List of tasks finished with error %d - %s.",
                                taskListResponse.getError().getCode(),
                                taskErrorMap.get(taskListResponse.getError().getCode()));
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
                Optional<GeneralResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), GeneralResponse.class);
                if (jsonResponse.isPresent()) {
                    GeneralResponse logoutResponse = jsonResponse.get();
                    if (logoutResponse.isSuccess()) {
                        logger.info("Logout finished.");
                    } else {
                        String logMsg = String.format("Logout with error %d - %s.",
                                logoutResponse.getError().getCode(),
                                authErrorMap.get(logoutResponse.getError().getCode()));
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
