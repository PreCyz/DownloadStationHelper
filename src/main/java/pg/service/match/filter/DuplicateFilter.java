package pg.service.match.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.StringUtils;
import pg.web.torrent.TorrentDetail;

import java.util.LinkedList;
import java.util.List;

/** Created by Gawa 2017-10-30 */
class DuplicateFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(DuplicateFilter.class);

    private final int MAX_DIFFERENCE_COUNTER = 2;

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        List<TorrentDetail> filteredOut = new LinkedList<>();
        for (TorrentDetail torrent : torrents) {
            if (filteredOut.isEmpty()) {
                filteredOut.add(torrent);
            } else {
                List<TorrentDetail> list = new LinkedList<>(filteredOut);
                boolean duplicate = false;
                for (TorrentDetail torrentFromList : list) {
                    //check for duplicates only when different episodes and seasons
                    if (isDuplicate(torrent, torrentFromList)) {
                        duplicate = true;
                        if (Long.valueOf(torrentFromList.getSize()) < Long.valueOf(torrent.getSize())) {
                            filteredOut.remove(torrentFromList);
                            filteredOut.add(torrent);
                        }
                        break;
                    }
                }
                if (!duplicate) {
                    filteredOut.add(torrent);
                }
            }
        }
        logger.info("Duplicate filter applied.");
        return filteredOut;
    }

    private boolean isDuplicate(TorrentDetail torrent, TorrentDetail torrentFromList) {
        return !StringUtils.nullOrTrimEmpty(torrent.getSeason())
                && !StringUtils.nullOrTrimEmpty(torrent.getEpisode())
                && torrent.getSeason().equalsIgnoreCase(torrentFromList.getSeason())
                && torrent.getEpisode().equalsIgnoreCase(torrentFromList.getEpisode())
                && isTitlesDuplicate(torrentFromList.getTitle(), torrent.getTitle());
    }

    protected boolean isTitlesDuplicate(String titleFromList, String currentTitle) {
        if (titleFromList.equalsIgnoreCase(currentTitle)) {
            return true;
        }
        String[] splitTitleFromList = titleFromList.split(" ");
        String[] splitCurrentTitle = currentTitle.split(" ");
        int minLength = Math.min(splitTitleFromList.length, splitCurrentTitle.length);
        int differenceCounter = 0;
        for (int idx = 0; idx < minLength; idx++) {
            if (!splitCurrentTitle[idx].equalsIgnoreCase(splitTitleFromList[idx])) {
                differenceCounter++;
            }
            if (differenceCounter == MAX_DIFFERENCE_COUNTER) {
                return false;
            }
        }
        return true;
    }
}
