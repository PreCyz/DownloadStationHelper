package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.JsonUtils;
import pg.util.StringUtils;
import pg.web.model.SettingKeys;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.TorrentResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**Created by Gawa 2017-09-15*/
public class FileServiceImpl implements FileService {

    private static final Logger logger = LogManager.getLogger(FileServiceImpl.class);

    private final String imdbFileName = "imdbTitleMap";
    private final Properties application;
    private Map<String, String> imdbTitleMap;

    public FileServiceImpl(Properties application) {
        this.application = application;
    }

    @Override
    public void writeTorrentsToFile(List<ReducedDetail> foundTorrents) {
        String fileName = new SimpleDateFormat("yyyyMMdd-hhmmss").format(Calendar.getInstance().getTime());
        Path filePath = createFilePath(fileName);
        logger.info("Writing [{}] torrents to file [{}].", foundTorrents.size(), filePath);
        JsonUtils.writeToFile(filePath, foundTorrents);
        logger.info(JsonUtils.convertToString(foundTorrents));
    }

    @Override
    public void buildImdbMap(List<TorrentResponse> torrentResponses) {
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
}
