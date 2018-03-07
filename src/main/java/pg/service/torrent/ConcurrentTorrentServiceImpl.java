package pg.service.torrent;

import pg.ui.window.controller.task.atomic.GetTorrentsTask;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Created by Gawa 2017-10-25 */
public class ConcurrentTorrentServiceImpl extends AbstractTorrentService {

    @Override
    public List<TorrentDetail> findTorrents() {
        List<GetTorrentsTask> tasks = createGetTorrentsTasks();
        List<TorrentResponse> torrentResponses = new ArrayList<>();
        executeTasks(tasks, torrentResponses);
        return torrentResponses.stream()
                .flatMap(tr -> tr.getTorrents().stream())
                .collect(Collectors.toList());
    }
}
