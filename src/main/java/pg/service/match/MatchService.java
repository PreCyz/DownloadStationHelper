package pg.service.match;

import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

import java.util.List;

/**Created by Gawa 2017-09-15*/
public interface MatchService {
    void filterTorrents(List<TorrentDetail> torrents);
    boolean hasFoundMatchingTorrents();
    List<ReducedDetail> getMatchingTorrents();
}
