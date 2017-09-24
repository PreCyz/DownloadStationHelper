package pg.service;

import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

import java.util.List;

/**Created by Gawa 2017-09-15*/
public interface FileService {
    void writeTorrentsToFile(List<ReducedDetail> foundTorrents);
    void buildImdbMap(List<TorrentDetail> torrentResponses);
    void writeImdbMapToFile();
}
