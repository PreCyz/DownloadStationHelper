package pg.ui.task.atomic.call;

import pg.factory.MatchServiceFactory;
import pg.service.match.MatchService;
import pg.web.model.ProgramMode;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

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
