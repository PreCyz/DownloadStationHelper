package pg.filter;

import pg.web.model.torrent.TorrentDetail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pawel Gawedzki on 9/19/2017.
 */
public class FileSizeFilter implements Filter {

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

    public FileSizeFilter(String maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        final long maxFileSize = extractMaxFileSize();
        if (maxFileSize == 0) {
            return torrents;
        }
        return torrents.stream().filter(torrent -> Long.valueOf(torrent.getSize()) <= maxFileSize).collect(Collectors.toList());
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
