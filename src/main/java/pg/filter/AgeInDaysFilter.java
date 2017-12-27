package pg.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.web.model.torrent.TorrentDetail;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**Created by Pawel Gawedzki on 9/19/2017.*/
class AgeInDaysFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(AgeInDaysFilter.class);

    private final int torrentAge;

    public AgeInDaysFilter(int torrentAge) {
        this.torrentAge = torrentAge;
    }

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -torrentAge);
        final long timestamp = yesterday.getTimeInMillis() / 1000;
        if (torrentAge == 0) {
            return torrents;
        }
        logger.info("Age in last {} days filter applied.", torrentAge);
        return torrents.stream()
                .filter(torrent -> torrent.getDateReleased() > timestamp)
                .collect(Collectors.toList());
    }
}
