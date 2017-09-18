package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.loader.ApplicationPropertiesLoader;
import pg.util.StringUtils;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.TorrentResponse;

import java.nio.file.Path;
import java.util.*;

/**Created by Gawa 2017-09-15*/
public class FileServiceImpl implements FileService {

    private static final Logger logger = LogManager.getLogger(FileServiceImpl.class);

    private Map<String, String> imdbTitleMap;

    @Override
    public void writeTorrentsToFile(List<ReducedDetail> foundTorrents) {
        Path filePath = AppConstants.fullFilePath(AppConstants.MATCHING_TORRENTS_FILE);
        Map<String, ReducedDetail> torrentsMap = new TreeMap<>();
        foundTorrents.forEach(reducedDetail -> torrentsMap.put(reducedDetail.getTitle(), reducedDetail));
        JsonUtils.writeToFile(filePath, torrentsMap);
        logger.info("[{}] torrents was written to file [{}].", torrentsMap.size(), filePath);
    }

    @Override
    public void buildImdbMap(List<TorrentResponse> torrentResponses) {
        imdbTitleMap = new TreeMap<>(Comparator.comparing(Integer::valueOf));
        torrentResponses.stream()
                .flatMap(torrentResponse -> torrentResponse.getTorrents().stream())
                .filter(torrent -> !StringUtils.nullOrTrimEmpty(torrent.getImdbId()))
                .forEach(torrent -> imdbTitleMap.put(torrent.getImdbId(), torrent.getTitle()));
        logger.info("Found [{}] unique imdb ids.", imdbTitleMap.size());
    }

    @Override
    public void writeImdbMapToFile() {
        if (!imdbTitleMap.isEmpty()) {
            Path filePath = AppConstants.fullFilePath(AppConstants.IMDB_FILE_NAME);
            Optional<Map> mapOpt = JsonUtils.convertFromFile(filePath, Map.class);
            imdbTitleMap.putAll(mapOpt.orElse(Collections.emptyMap()));
            JsonUtils.writeToFile(filePath, imdbTitleMap);
        } else {
            logger.info("No new imdb ids where found.");
        }
    }
}
