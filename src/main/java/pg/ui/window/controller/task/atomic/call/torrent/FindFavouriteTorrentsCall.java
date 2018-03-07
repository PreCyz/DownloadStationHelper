package pg.ui.window.controller.task.atomic.call.torrent;

import pg.service.torrent.FavouriteTorrentServiceImpl;
import pg.service.torrent.TorrentService;
import pg.ui.window.controller.completable.UpdatableTask;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2018-1-6 */
public class FindFavouriteTorrentsCall implements Callable<List<TorrentDetail>> {

    private final UpdatableTask<?> fxTask;

    public FindFavouriteTorrentsCall(UpdatableTask<?> fxTask) {
        this.fxTask = fxTask;
    }

    @Override
    public List<TorrentDetail> call() {
        TorrentService torrentService = new FavouriteTorrentServiceImpl(fxTask);
        return torrentService.findTorrents();
    }
}
