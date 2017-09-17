package pg.service;

import pg.web.model.torrent.ReducedDetail;
import pg.web.response.TorrentResponse;

import java.util.List;

/**Created by Gawa 2017-09-15*/
public interface MatchService {
    void filterTorrents(List<TorrentResponse> torrents);
    boolean hasFoundMatchingTorrents();
    List<ReducedDetail> getMatchingTorrents();
}
