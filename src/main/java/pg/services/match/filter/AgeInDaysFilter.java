package pg.services.match.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.web.torrent.TorrentDetail;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**Created by Pawel Gawedzki on 9/19/2017.*/
class AgeInDaysFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AgeInDaysFilter.class);

    private final int torrentAge;

    AgeInDaysFilter(int torrentAge) {
        this.torrentAge = torrentAge;
    }

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        if (torrentAge == 0) {
            return torrents;
        }
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -torrentAge);
        final long timestamp = yesterday.getTimeInMillis() / 1000;
        List<TorrentDetail> filteredOut = torrents.stream()
                .filter(torrent -> torrent.getDateReleased() > timestamp)
                .collect(Collectors.toList());
        logger.info("Age in last {} days filter applied. {} torrents were filtered out. {} torrents remained.",
                torrentAge, torrents.size() - filteredOut.size(), filteredOut.size());
        return filteredOut;
    }
}
