package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.JsonUtils;
import pg.util.StringUtils;
import pg.web.client.GetClient;
import pg.web.model.ApiDetails;
import pg.web.model.SettingKeys;
import pg.web.model.ShowKeys;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**Created by Gawa on 15/08/17*/
public abstract class AbstractExecutor implements Executor {

    private static final Logger logger = LogManager.getLogger(AbstractExecutor.class);

    protected final int defaultLimit = 100;
    protected final int defaultPage = 1;
    protected final int defaultTorrentAge = 0;
    protected final String defaultUrl = "https://eztv.ag/api/get-torrents";
    protected final int defaultServerPort = 5001;
    protected final Properties shows;
    protected final Properties application;
    protected ApiDetails authInfo;
    protected ApiDetails downloadStationTask;
    protected SearchService searchService;
    protected List<ReducedDetail> foundTorrents;
    protected String sid;

    private final String imdbFileName = "imdbTitleMap";
    private List<TorrentResponse> torrentResponses;
    private Map<String, String> imdbTitleMap;
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
        searchService = new SearchService(Integer.valueOf(
                application.getProperty(SettingKeys.TORRENT_AGE.key(), String.valueOf(defaultTorrentAge))
        ));
        foundTorrents = new LinkedList<>();
        torrentResponses = new LinkedList<>();
    }

    @Override
    public void findTorrents() {
        int pages = Integer.valueOf(application.getProperty(SettingKeys.PAGE.key(), String.valueOf(defaultPage)));
        for (int page = defaultPage; page <= pages; page++) {
            String url = prepareTorrentUrl(page);
            logger.info("Executing request for url {}", url);
            GetClient client = new GetClient(url);
            if (client.get().isPresent()) {
                String json = client.get().get();
                Optional<TorrentResponse> response = JsonUtils.convertFromString(json, TorrentResponse.class);
                response.ifPresent(torrentResponse -> torrentResponses.add(torrentResponse));
            } else {
                logger.info("No response for url {}", url);
            }
        }
        logger.info(torrentResponses);
    }

    protected String prepareTorrentUrl(int currentPage) {
        String url = application.getProperty(SettingKeys.URL.key(), defaultUrl);
        String limit = String.format("limit=%s", application.getProperty(SettingKeys.LIMIT.key(),
                String.valueOf(defaultLimit)));
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limit, page);
    }

    @Override
    public void matchTorrents() {
        List<TorrentDetail> torrentDetails = torrentResponses.stream()
                .flatMap(torrentResponse -> torrentResponse.getTorrents().stream())
                .collect(Collectors.toList());
        Map<String, Integer> map = buildPrecisionWordMap();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            searchService.setMatchPrecision(entry.getValue());
            foundTorrents.addAll(searchService.search(entry.getKey(), torrentDetails));
        }
    }

    @Override
    public void writeTorrentsToFile() {
        String fileName = new SimpleDateFormat("yyyyMMdd-hhmmss").format(Calendar.getInstance().getTime());
        Path filePath = createFilePath(fileName);
        logger.info("Writing [{}] torrents to file [{}].", foundTorrents.size(), filePath);
        JsonUtils.writeToFile(filePath, foundTorrents);
        logger.info(JsonUtils.convertToString(foundTorrents));
    }

    @Override
    public void buildImdbMap() {
        imdbTitleMap = new HashMap<>();
        torrentResponses.stream()
                .flatMap(torrentResponse -> torrentResponse.getTorrents().stream())
                .filter(torrent -> !StringUtils.nullOrEmpty(torrent.getImdbId()))
                .forEach(torrent -> imdbTitleMap.put(torrent.getImdbId(), torrent.getTitle()));
        logger.info("Found [{}] unique imdb ids.", imdbTitleMap.size());
    }

    @Override
    public void writeImdbMapToFile() {
        if (!imdbTitleMap.isEmpty()) {
            Path filePath = createFilePath(imdbFileName);
            Optional<Map> mapOpt = JsonUtils.convertFromFile(filePath, Map.class);
            imdbTitleMap.putAll(mapOpt.orElse(Collections.emptyMap()));
            JsonUtils.writeToFile(filePath, imdbTitleMap);
        } else {
            logger.info("No new imdb ids where found.");
        }
    }

    protected Map<String, Integer> buildPrecisionWordMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Object keyObject : shows.keySet()) {
            String key = (String) keyObject;
            if (key.endsWith(ShowKeys.baseWords.name())) {
                String baseWords = shows.getProperty(key);
                if (baseWords != null && baseWords.trim().length() > 0) {
                    String precisionKey = key.substring(0, key.indexOf(ShowKeys.baseWords.name())) +
                            ShowKeys.matchPrecision.name();
                    String precision = shows.getProperty(precisionKey,
                            String.valueOf(baseWords.split(",").length));
                    Integer matchPrecision = Integer.valueOf(precision);
                    map.put(baseWords, matchPrecision);
                }
            }
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException("No shows were specified. Add some base words to shows.properties.");
        }
        return map;
    }

    protected Path createFilePath(String fileName) {
        String directoryPath = application.getProperty(SettingKeys.FILE_PATH.key());
        if (Files.notExists(Paths.get(directoryPath))) {
            try {
                Files.createDirectory(Paths.get(directoryPath));
            } catch(IOException ex) {
                throw new IllegalArgumentException(ex.getLocalizedMessage());
            }
        }
        String filePath = String.format("%s/%s.json", directoryPath, fileName);
        return Paths.get(filePath).toAbsolutePath();
    }

    @Override
    public void prepareAvailableOperations() {
        String requestUrl = prepareServerUrl();
        if (requestUrl.isEmpty()) {
            logger.info("Server URL not specified.");
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

    @Override
    public void loginToDiskStation() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildLoginUrl(serverUrl);
            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get(createCookieMap());
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
                Optional<String> response = client.get(createCookieMap());
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

    @Override
    public void listOfTasks() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
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

    @Override
    public void logoutFromDiskStation() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
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

    @Override
    public void writeTorrentsOnDS() {
        String destination = application.getProperty(SettingKeys.TORRENT_LOCATION.key(), "");
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
    public boolean hasFoundTorrents() {
        return foundTorrents != null && !foundTorrents.isEmpty();
    }

    protected abstract String buildLoginUrl(String serverUrl);
    protected abstract String buildCreateTaskUrl(String serverUrl);
    protected abstract String buildTaskListUrl(String serverUrl);
    protected abstract String buildLogoutUrl(String serverUrl);
    protected abstract Map<String, String> createCookieMap();
}
