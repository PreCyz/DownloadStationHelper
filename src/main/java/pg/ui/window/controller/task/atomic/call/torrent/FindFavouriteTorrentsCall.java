package pg.ui.window.controller.task.atomic.call.torrent;

import pg.services.torrent.FavouriteTorrentService;
import pg.services.torrent.TorrentService;
import pg.ui.window.controller.completable.UpdatableTask;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2018-1-6 */
public class FindFavouriteTorrentsCall implements Callable<List<TorrentDetail>> {

    private TorrentService torrentService;

    public FindFavouriteTorrentsCall(UpdatableTask<?> fxTask) {
        torrentService = new FavouriteTorrentService(fxTask);
    }

    @Override
    public List<TorrentDetail> call() {
        return torrentService.findTorrents();
    }
}
