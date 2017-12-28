package pg.ui.window.controller.task.atomic.call.torrent;

import pg.program.ProgramMode;
import pg.service.match.MatchService;
import pg.service.match.MatchServiceFactory;
import pg.web.torrent.ReducedDetail;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-29 */
public class MatchTorrentsCall implements Callable<List<ReducedDetail>> {

    private final ProgramMode programMode;
    private final List<TorrentDetail> torrents;

    public MatchTorrentsCall(ProgramMode programMode, List<TorrentDetail> torrents) {
        this.programMode = programMode;
        this.torrents = torrents;
    }

    @Override
    public List<ReducedDetail> call() throws Exception {
        MatchService matchService = MatchServiceFactory.getMatchService(programMode);
        matchService.match(torrents);
        return matchService.getMatchingTorrents();
    }

}
