package pg.ui.window.controller.task.atomic.call.torrent;

import pg.program.ProgramMode;
import pg.service.torrent.TorrentService;
import pg.service.torrent.TorrentServiceFactory;
import pg.ui.window.controller.completable.UpdatableTask;
import pg.util.StringUtils;
import pg.web.torrent.TorrentDetail;

import java.util.List;

/** Created by Gawa 2018-1-6 */
public class FindTorrentsWithTaskCall extends FindTorrentsCall {

    private String imdbId;
    private final ProgramMode programMode;
    private final UpdatableTask<?> fxTask;

    public FindTorrentsWithTaskCall(UpdatableTask<?> fxTask) {
        this.fxTask = fxTask;
        this.programMode = ProgramMode.ALL_CONCURRENT;
    }

    public FindTorrentsWithTaskCall(String imdbId, UpdatableTask<?> fxTask) {
        this.fxTask = fxTask;
        this.programMode = ProgramMode.IMDB;
        this.imdbId = imdbId;
    }

    @Override
    public List<TorrentDetail> call() {
        TorrentService torrentService;
        if (StringUtils.nullOrTrimEmpty(imdbId)) {
            torrentService = TorrentServiceFactory.getTorrentService(programMode, fxTask);
        } else {
            torrentService = TorrentServiceFactory.getTorrentService(imdbId);
        }
        return torrentService.findTorrents();
    }

}
