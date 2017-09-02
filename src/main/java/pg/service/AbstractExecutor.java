package pg.service;

import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.ApiDetails;
import pg.web.model.SettingKeys;
import pg.web.model.ShowKeys;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**Created by Gawa on 15/08/17*/
public abstract class AbstractExecutor implements Executor {

    protected final String defaultLimit = "100";
    protected final String defaultPage = "1";
    protected final String defaultUrl = "https://eztv.ag/api/get-torrents";
    protected final int defaultServerPort = 5001;
    protected final Properties shows;
    protected final Properties application;
    protected ApiDetails authInfo;
    protected ApiDetails downloadStationTask;
    protected SearchService searchService;
    protected List<ReducedDetail> foundTorrents;
    protected String sid;

    private TorrentResponse torrentResponse;
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

    public AbstractExecutor(Properties shows, Properties application) {
        this.shows = shows;
        this.application = application;
        searchService = new SearchService();
        foundTorrents = new LinkedList<>();
    }

    public void findTorrents() {
        GetClient client = new GetClient(prepareTorrentUrl());
        if (client.get().isPresent()) {
            String json = client.get().get();
            Optional<TorrentResponse> response = JsonUtils.convertFromString(json, TorrentResponse.class);
            response.ifPresent(torrentResponse -> this.torrentResponse = torrentResponse);
            System.out.println(this.torrentResponse);
        } else {
            System.out.println("No torrents to process");
            System.exit(0);
        }
    }

    protected String prepareTorrentUrl() {
        String url = application.getProperty(SettingKeys.URL.key(), defaultUrl);
        String limit = String.format("limit=%s", application.getProperty(SettingKeys.LIMIT.key(), defaultLimit));
        String page = String.format("page=%s", application.getProperty(SettingKeys.PAGE.key(), defaultPage));
        return String.format("%s?%s&%s", url, limit, page);
    }

    public void matchTorrents() {
        Map<String, Integer> map = buildPrecisionWordMap();
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            searchService.setMatchPrecision(entry.getValue());
            foundTorrents.addAll(searchService.search(entry.getKey(), torrentResponse.getTorrents()));
        }
    }

    public void writeTorrentsToFile() {
        if (!foundTorrents.isEmpty()) {
            System.out.printf("Found %s matching torrents.%n", foundTorrents.size());
            if ("Y".equals(application.getProperty(SettingKeys.WRITE_TO_FILE.key(), "N"))) {
                JsonUtils.writeToFile(createFilePath(), foundTorrents);
            }
            System.out.println(JsonUtils.convertToString(foundTorrents));
        } else {
            System.out.printf("No matching torrents found.%n");
        }
    }

    protected Map<String, Integer> buildPrecisionWordMap() {
        Map<String, Integer> map = new HashMap<>();
        for(Object keyObject : shows.keySet()) {
            String key = (String) keyObject;
            if (key.endsWith(ShowKeys.baseWords.name())) {
                String baseWords = shows.getProperty(key);
                String precisionKey = key.substring(0, key.indexOf(ShowKeys.baseWords.name())) +
                        ShowKeys.matchPrecision.name();
                String precision = shows.getProperty(precisionKey,
                        String.valueOf(baseWords.split(",").length));
                Integer matchPrecision = Integer.valueOf(precision);
                map.put(baseWords, matchPrecision);
            }
        }
        return map;
    }

    protected Path createFilePath() {
        String fileName = new SimpleDateFormat("yyyyMMdd-hhmmss").format(Calendar.getInstance().getTime());
        String filePath = String.format("%s/%s.json", application.getProperty(SettingKeys.FILE_PATH.key()), fileName);
        return Paths.get(filePath).toAbsolutePath();
    }

    public void prepareAvailableOperations() {
        String requestUrl = prepareServerUrl();
        if (requestUrl.isEmpty()) {
            System.out.printf("Server URL not specified");
        } else {
            requestUrl += application.getProperty(SettingKeys.API_INFO.key());
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
        String server = application.getProperty(SettingKeys.SERVER_URL.key());
        if (server != null && !server.isEmpty()) {
            String port = application.getProperty(SettingKeys.SERVER_PORT.key(), String.valueOf(defaultServerPort));
            String protocol = port.equals(String.valueOf(defaultServerPort)) ? "https" : "http";
            return serverUrl = String.format("%s://%s:%s", protocol, server, port);
        }
        return serverUrl = "";
    }

    public void loginToDiskStation() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            System.out.printf("Server URL not specified");
        } else {
            String requestUrl = buildLoginUrl(serverUrl);
            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get(createCookieMap());
            if (response.isPresent()) {
                Optional<LoginResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), LoginResponse.class);
                System.out.println("Login format sid.");
                if (jsonResponse.isPresent()) {
                    LoginResponse loginResponse = jsonResponse.get();
                    if (loginResponse.isSuccess()) {
                        sid = loginResponse.getLoginDetails().getSid();
                        System.out.printf("Login successful. sid = %s.%n", sid);
                    } else {
                        System.out.printf("Login unsuccessful. Details %d - %s%n",
                                loginResponse.getError().getCode(),
                                authErrorMap.get(loginResponse.getError().getCode()));
                    }
                } else {
                    System.out.println("Login unsuccessful. No response from server.");
                }

            }
        }
    }

    public void createDownloadStationTasks() {
        if (!foundTorrents.isEmpty()) {
            String serverUrl = prepareServerUrl();
            if (serverUrl.isEmpty()) {
                System.out.printf("Server URL not specified");
            } else {
                String requestUrl = buildCreateTaskUrl(serverUrl);
                System.out.printf("RestURL: [%s].%n", requestUrl);

                GetClient client = new GetClient(requestUrl);
                Optional<String> response = client.get(createCookieMap());
                if (response.isPresent()) {
                    Optional<GeneralResponse> jsonResponse = JsonUtils.convertFromString(response.get(), GeneralResponse.class);
                    if (jsonResponse.isPresent()) {
                        GeneralResponse createTaskResponse = jsonResponse.get();
                        if (createTaskResponse.isSuccess()) {
                            System.out.println("Task creation successful.");
                        } else {
                            System.out.printf("Task creation finished with error %d - %s.%n",
                                    createTaskResponse.getError().getCode(),
                                    taskErrorMap.get(createTaskResponse.getError().getCode()));
                        }
                    } else {
                        System.out.println("Task creation with error. No details.");
                    }
                }
            }
        } else {
            System.out.println("No task to create.");
        }
    }

    public void listOfTasks() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            System.out.printf("Server URL not specified");
        } else {
            String requestUrl = buildTaskListUrl(serverUrl);

            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get(createCookieMap());
            if (response.isPresent()) {
                Optional<TaskListResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), TaskListResponse.class);
                if (jsonResponse.isPresent()) {
                    TaskListResponse taskListResponse = jsonResponse.get();
                    if (taskListResponse.isSuccess()) {
                        System.out.printf("Total number of tasks on download station is: %d.%n",
                                taskListResponse.getTaskListDetail().getTotal());
                    } else {
                        System.out.printf("List of tasks finished with error %d - %s.%n",
                                taskListResponse.getError().getCode(),
                                taskErrorMap.get(taskListResponse.getError().getCode()));
                    }
                } else {
                    System.out.println("List of tasks with error. No details.");
                }
            }
        }
    }

    public void logoutFromDiskStation() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            System.out.printf("Server URL not specified");
        } else {
            String requestUrl = buildLogoutUrl(serverUrl);
            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get(createCookieMap());
            if (response.isPresent()) {
                Optional<GeneralResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), GeneralResponse.class);
                if (jsonResponse.isPresent()) {
                    GeneralResponse logoutResponse = jsonResponse.get();
                    if (logoutResponse.isSuccess()) {
                        System.out.println("Logout finished.");
                    } else {
                        System.out.printf("Logout with error %d - %s.%n",
                                logoutResponse.getError().getCode(),
                                authErrorMap.get(logoutResponse.getError().getCode()));
                    }
                } else {
                    System.out.println("Logout with error. No details.");
                }
            }
        }
    }

    public void writeTorrentsOnDS() {
        String destination = application.getProperty(SettingKeys.TORRENT_LOCATION.key(), "");
        if (destination == null || destination.isEmpty()) {
            System.out.printf("%s not specified. Add it to application.properties.%n", SettingKeys.TORRENT_LOCATION.key());
            return;
        }
        if (foundTorrents.isEmpty()) {
            System.out.println("No task to create.");
            return;
        }
        foundTorrents.forEach(torrent -> {
            String filePath = destination + torrent.getTitle() + ".torrent";
            new GetClient(torrent.getTorrentUrl()).downloadFile(filePath);
            System.out.printf("File [%s] saved in %s.%n", torrent.getTitle(), destination);
        });
    }

    protected abstract String buildLoginUrl(String serverUrl);
    protected abstract String buildCreateTaskUrl(String serverUrl);
    protected abstract String buildTaskListUrl(String serverUrl);
    protected abstract String buildLogoutUrl(String serverUrl);
    protected abstract Map<String, String> createCookieMap();
}
