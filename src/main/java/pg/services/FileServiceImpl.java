package pg.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.util.StringUtils;
import pg.web.torrent.ReducedDetail;
import pg.web.torrent.TorrentDetail;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-15*/
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private Map<String, String> imdbTitleMap;

    @Override
    @SuppressWarnings({"rawtypes","unchecked"})
    public void writeTorrentsToFile(List<ReducedDetail> foundTorrents) {
        Path filePath = AppConstants.fullFilePath(AppConstants.MATCHING_TORRENTS_FILE);
        Optional<TreeMap> treeMap = JsonUtils.convertFromFile(filePath, TreeMap.class);
        Map<String, ReducedDetail> torrentsMap = treeMap.orElse(new TreeMap<String, ReducedDetail>());
        torrentsMap.putAll(foundTorrents.stream()
                .collect(Collectors.toMap(ReducedDetail::getTitle, reducedDetail -> reducedDetail))
        );
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
    @SuppressWarnings("unchecked")
    public void writeImdbMapToFile() {
        if (!imdbTitleMap.isEmpty()) {
            Path filePath = AppConstants.fullFilePath(AppConstants.IMDB_FILE_NAME);
            Map<String, String> existingImdbMap = JsonUtils.convertFromFile(filePath, TreeMap.class)
                    .orElse(new TreeMap<String, String>());
            imdbTitleMap.putAll(existingImdbMap);
            JsonUtils.writeToFile(filePath, imdbTitleMap);
            int newImdbNumber = imdbTitleMap.size() - existingImdbMap.size();
            logger.info("[{}] new imdb numbers was added to file. There are [{}] unique imdb numbers " +
                            "stored in the file {}.",
                    newImdbNumber, imdbTitleMap.size(), AppConstants.IMDB_FILE_NAME);
        } else {
            logger.info("No new imdb ids where found.");
        }
    }

    @Override
    public int getImdbMapSize() {
        if (imdbTitleMap != null) {
            return imdbTitleMap.size();
        }
        return 0;
    }
}
