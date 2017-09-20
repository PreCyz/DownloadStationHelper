package pg.service;

import pg.web.response.TorrentResponse;

import java.util.List;

/**Created by Gawa 2017-09-15*/
public interface TorrentService {
    List<TorrentResponse> findTorrents();
    List<TorrentResponse> findTorrentsByImdbId();
}
