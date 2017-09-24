package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.util.StringUtils;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

import java.nio.file.Path;
import java.util.*;

/**Created by Gawa 2017-09-15*/
public class FileServiceImpl implements FileService {

    private static final Logger logger = LogManager.getLogger(FileServiceImpl.class);

    private Map<String, String> imdbTitleMap;

    @Override
    public void writeTorrentsToFile(List<ReducedDetail> foundTorrents) {
        Path filePath = AppConstants.fullFilePath(AppConstants.MATCHING_TORRENTS_FILE);
        Optional<TreeMap> treeMap = JsonUtils.convertFromFile(filePath, TreeMap.class);
        Map<String, ReducedDetail> torrentsMap = treeMap.orElse(new TreeMap<String, ReducedDetail>());
        foundTorrents.forEach(reducedDetail -> torrentsMap.put(reducedDetail.getTitle(), reducedDetail));
        JsonUtils.writeToFile(filePath, torrentsMap);
        logger.info("[{}] torrents stored in file [{}].", torrentsMap.size(), filePath);
    }

    @Override
    public void buildImdbMap(List<TorrentDetail> torrents) {
        imdbTitleMap = new TreeMap<>(Comparator.comparing(Integer::valueOf));
        torrents.stream()
                .filter(torrent -> !StringUtils.nullOrTrimEmpty(torrent.getImdbId()))
                .forEach(torrent -> imdbTitleMap.put(torrent.getImdbId(), torrent.getTitle()));
        logger.info("Found [{}] unique imdb ids.", imdbTitleMap.size());
    }

    @Override
    public void writeImdbMapToFile() {
        if (!imdbTitleMap.isEmpty()) {
            Path filePath = AppConstants.fullFilePath(AppConstants.IMDB_FILE_NAME);
            Optional<TreeMap> mapOpt = JsonUtils.convertFromFile(filePath, TreeMap.class);
            imdbTitleMap.putAll(mapOpt.orElse(new TreeMap<String,String>()));
            JsonUtils.writeToFile(filePath, imdbTitleMap);
        } else {
            logger.info("No new imdb ids where found.");
        }
    }
}
