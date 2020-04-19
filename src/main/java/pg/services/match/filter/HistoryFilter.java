package pg.services.match.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.web.torrent.ReducedDetail;
import pg.web.torrent.TorrentDetail;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**Created by Pawel Gawedzki on 9/19/2017.*/
class HistoryFilter extends DuplicateFilter {

    private static final Logger logger = LogManager.getLogger(HistoryFilter.class);

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        Map<String, ReducedDetail> map = JsonUtils.convertMatchTorrentsFromFile(
                AppConstants.fullFilePath(AppConstants.MATCHING_TORRENTS_FILE)
        );
        if (map.isEmpty()) {
            return torrents;
        }
        List<TorrentDetail> filteredOut = torrents.stream()
                    .filter(torrentDetail -> !map.containsKey(torrentDetail.getTitle()))
                    .collect(Collectors.toList());
        logger.info("History filter applied. {} torrents were filtered out. {} torrents remained.",
                    torrents.size() - filteredOut.size(), filteredOut.size());

        List<TorrentDetail> filteredOutWithoutDuplicates = filterDuplicates(filteredOut, map);
        logger.info("Duplicates on history filter applied. {} torrents were filtered out. {} torrents remained.",
                filteredOut.size() - filteredOutWithoutDuplicates.size(), filteredOutWithoutDuplicates.size());
        return filteredOutWithoutDuplicates;
    }

    private List<TorrentDetail> filterDuplicates(List<TorrentDetail> torrents, Map<String, ReducedDetail> map) {
        List<TorrentDetail> filteredOut = new LinkedList<>();
        for (TorrentDetail torrent : torrents) {
            boolean duplicate = false;
            for (String key : map.keySet()) {
                ReducedDetail historicTorrent = map.get(key);
                if (isDuplicate(torrent, historicTorrent)) {
                    logger.info("Historic duplicate. '{}' duplicates already downloaded '{}'",
                            torrent.getTitle(), historicTorrent.getTitle());
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                filteredOut.add(torrent);
            }
        }
        return filteredOut;
    }

}
