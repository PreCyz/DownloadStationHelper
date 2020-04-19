package pg.ui.window.controller.task.atomic.call.torrent;

import pg.services.torrent.ImdbTorrentServiceImpl;
import pg.services.torrent.TorrentService;
import pg.ui.window.controller.completable.UpdatableTask;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-29 */
public class FindImdbTorrentsCall implements Callable<List<TorrentDetail>> {

    private final UpdatableTask<?> fxTask;
    protected String imdbId;

    public FindImdbTorrentsCall(String imdbId, UpdatableTask<?> fxTask) {
        this.imdbId = imdbId;
        this.fxTask = fxTask;
    }

    @Override
    public List<TorrentDetail> call() {
        TorrentService torrentService = new ImdbTorrentServiceImpl(imdbId, fxTask);
        return torrentService.findTorrents();
    }

}
