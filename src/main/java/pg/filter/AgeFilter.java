package pg.filter;

import pg.web.model.torrent.TorrentDetail;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**Created by Pawel Gawedzki on 9/19/2017.*/
public class AgeFilter implements Filter {

    private final int torrentAge;

    public AgeFilter(int torrentAge) {
        this.torrentAge = torrentAge;
    }

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -torrentAge);
        final long timestamp = yesterday.getTimeInMillis() / 1000;
        return torrents.stream()
                .filter(torrent -> torrent.getDateReleased() > timestamp)
                .collect(Collectors.toList());
    }
}
