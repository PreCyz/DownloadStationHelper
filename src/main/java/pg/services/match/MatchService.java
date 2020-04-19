package pg.services.match;

import pg.web.torrent.ReducedDetail;
import pg.web.torrent.TorrentDetail;

import java.util.List;

/**Created by Gawa 2017-09-15*/
public interface MatchService {
    void match(List<TorrentDetail> torrents);
    boolean hasFoundMatchingTorrents();
    List<ReducedDetail> getMatchingTorrents();
}
