package pg.service.match.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.stream.Collectors;

/**Created by Pawel Gawedzki on 9/19/2017.*/
class FileSizeFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(FileSizeFilter.class);

    private final String maxSize;

    private enum Units {
        G(1000000000),
        M(1000000),
        K(1000);

        int scale;

        Units(int scale) {
            this.scale = scale;
        }
    }

    FileSizeFilter(String maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        final long maxFileSize = extractMaxFileSize();
        if (maxFileSize == 0) {
            return torrents;
        }
        List<TorrentDetail> filteredOut = torrents.stream()
                .filter(torrent -> Long.valueOf(torrent.getSize()) <= maxFileSize)
                .collect(Collectors.toList());
        logger.info("Max file size filter applied. {} torrents were filtered out. {} torrents remained.",
                torrents.size() - filteredOut.size(), filteredOut.size());
        return filteredOut;
    }

    private long extractMaxFileSize() {
        try {
            Units unit = Units.valueOf(maxSize.substring(maxSize.length() - 1));
            Double maxFileSize = Double.valueOf(maxSize.substring(0, maxSize.length() - 1));
            return (long) (maxFileSize * unit.scale);
        } catch (Exception ex) {
            return 0;
        }
    }

}
