package pg.service.torrent;

import pg.program.ProgramMode;
import pg.ui.window.controller.completable.UpdatableTask;

/** Created by Gawa 2017-09-23*/
public class TorrentServiceFactory {

    private TorrentServiceFactory() {}

    public static TorrentService getTorrentService(ProgramMode programMode) {
        switch (programMode) {
            case IMDB_COMAND_LINE:
                return new ImdbFromFileServiceImpl();
            default:
                return new ConcurrentTorrentServiceImpl();
        }
    }

    public static TorrentService getFavouriteTorrentService(UpdatableTask<?> fxTask) {
        return new FavouriteTorrentServiceImpl(fxTask);
    }

    public static TorrentService getImdbTorrentService(String imdbId, UpdatableTask<?> fxTask) {
        return new ImdbTorrentServiceImpl(imdbId, fxTask);
    }
}
