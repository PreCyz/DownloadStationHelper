package pg.ui.task.atomic.call;

import pg.factory.TorrentServiceFactory;
import pg.service.torrent.TorrentService;
import pg.util.StringUtils;
import pg.web.model.ProgramMode;
import pg.web.model.torrent.TorrentDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-29 */
public class FindTorrentsCall implements Callable<List<TorrentDetail>> {

    private String imdbId;
    private final ProgramMode programMode;

    public FindTorrentsCall() {
        this.programMode = ProgramMode.ALL_CONCURRENT;
    }

    public FindTorrentsCall(String imdbId) {
        this.programMode = ProgramMode.IMDB;
        this.imdbId = imdbId;
    }

    @Override
    public List<TorrentDetail> call() throws Exception {
        TorrentService torrentService;
        if (StringUtils.nullOrTrimEmpty(imdbId)) {
            torrentService = TorrentServiceFactory.getTorrentService(programMode);
        } else {
            torrentService = TorrentServiceFactory.getTorrentService(imdbId);
        }
        return torrentService.findTorrents();
    }

}