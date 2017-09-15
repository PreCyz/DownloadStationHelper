package pg.service;

import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

import java.util.List;

/**Created by Gawa 2017-09-15*/
public interface SearchService {
    List<ReducedDetail> search(final String baseWord, List<TorrentDetail> torrents);
    void setMatchPrecision(int matchPrecision);
}
