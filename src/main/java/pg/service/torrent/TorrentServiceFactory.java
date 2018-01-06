package pg.service.torrent;

import pg.program.ProgramMode;
import pg.ui.window.controller.completable.UpdatableTask;

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

    public static TorrentService getTorrentService(ProgramMode programMode, UpdatableTask<?> fxTask) {
        if (fxTask == null) {
            return getTorrentService(programMode);
        }
        switch (programMode) {
            case IMDB:
                return new TorrentImdbServiceImpl();
            case ALL_CONCURRENT:
                return new TorrentServiceWithTaskImpl(fxTask);
            default:
                return new TorrentServiceImpl();
        }
    }

    public static TorrentService getTorrentService(String imdbId) {
        return new TorrentImdbServiceImpl().withImdbId(imdbId);
    }
}
