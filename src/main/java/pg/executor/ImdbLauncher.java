package pg.executor;

import pg.service.TorrentService;
import pg.service.TorrentServiceImpl;
import pg.web.response.TorrentResponse;

import java.util.List;

/**Created by Pawel Gawedzki on 9/20/2017.*/
public class ImdbLauncher implements Runnable {

    @Override
    public void run() {
        TorrentService torrentService = new TorrentServiceImpl();
        List<TorrentResponse> torrents = torrentService.findTorrentsByImdbId();
    }
}
