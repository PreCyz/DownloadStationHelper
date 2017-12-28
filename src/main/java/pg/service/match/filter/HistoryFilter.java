package pg.service.match.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.util.StringUtils;
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
        if (!map.isEmpty()) {
            logger.info("History filter applied.");
            List<TorrentDetail> filteredOut = torrents.stream()
                    .filter(torrentDetail -> !map.containsKey(torrentDetail.getTitle()))
                    .collect(Collectors.toList());
            return filterDuplicates(filteredOut, map);
        }
        return torrents;
    }

    private List<TorrentDetail> filterDuplicates(List<TorrentDetail> torrents, Map<String, ReducedDetail> map) {
        List<TorrentDetail> filteredOut = new LinkedList<>();
        for (TorrentDetail torrent : torrents) {
            boolean duplicate = false;
            for (String key : map.keySet()) {
                ReducedDetail historicTorrent = map.get(key);
                if (isDuplicate(torrent, historicTorrent)) {
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

    private boolean isDuplicate(TorrentDetail torrent, ReducedDetail historicTorrent) {
        return !StringUtils.nullOrTrimEmpty(torrent.getSeason()) &&
                !StringUtils.nullOrTrimEmpty(torrent.getEpisode()) &&
                torrent.getSeason().equals(historicTorrent.getSeason()) &&
                torrent.getEpisode().equals(historicTorrent.getEpisode()) &&
                isTitlesDuplicate(torrent.getTitle(), historicTorrent.getTitle());
    }

}
