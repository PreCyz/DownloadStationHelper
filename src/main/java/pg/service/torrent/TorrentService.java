package pg.service.torrent;

import pg.web.torrent.TorrentDetail;

import java.util.List;

/**Created by Gawa 2017-09-15*/
public interface TorrentService {
    List<TorrentDetail> findTorrents();
}
