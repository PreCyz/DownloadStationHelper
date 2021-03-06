package pg.services.match.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.util.StringUtils;
import pg.web.torrent.TorrentDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-24*/
class ReleaseDateFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseDateFilter.class);

    private final String releaseDateString;
    private final SimpleDateFormat[] formats = new SimpleDateFormat[] {
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyyMMdd")
    };

    public ReleaseDateFilter(String releaseDate) {
        this.releaseDateString = releaseDate;
    }

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        long timestamp = getReleaseDateUnixTimestamp();
        if (timestamp <= 0) {
            return torrents;
        }
        List<TorrentDetail> filteredOut = torrents.stream()
                .filter(torrent -> torrent.getDateReleased() >= timestamp)
                .collect(Collectors.toList());
        logger.info("Release date filter applied. {} torrents were filtered out. {} torrents remained.",
                torrents.size() - filteredOut.size(), filteredOut.size());

        return filteredOut;
    }

    private long getReleaseDateUnixTimestamp() {
        if (StringUtils.nullOrTrimEmpty(releaseDateString)) {
            return 0;
        }
        for (SimpleDateFormat sdf : formats) {
            try {
                Date parse = sdf.parse(releaseDateString);
                return parse.getTime() / 1000;
            } catch (ParseException e) {}
        }
        return 0;
    }
}
