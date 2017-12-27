package pg.service.match.filter;

import pg.web.model.torrent.TorrentDetail;

import java.util.List;

/**Created by Pawel Gawedzki on 9/19/2017.*/
public interface Filter {
    List<TorrentDetail> apply(List<TorrentDetail> torrents);
}
