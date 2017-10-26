package pg.factory;

import pg.service.torrent.TorrentImdbServiceImpl;
import pg.service.torrent.TorrentService;
import pg.service.torrent.TorrentServiceImpl;
import pg.service.torrent.task.ConcurrentTorrentServiceImpl;
import pg.web.model.ProgramMode;

/**Created by Gawa 2017-09-23*/
public class TorrentServiceFactory {

    private TorrentServiceFactory() {}

    public static TorrentService getTorrentService(ProgramMode programMode) {
        switch (programMode) {
            case IMDB:
                return new TorrentImdbServiceImpl();
            case ALL_CONCURRENT:
                return new ConcurrentTorrentServiceImpl();
            default:
            return new TorrentServiceImpl();
        }
    }
}
