package pg.service.torrent;

import pg.props.ShowsPropertiesHelper;
import pg.ui.window.controller.task.atomic.GetTorrentsTask;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Created by Gawa 2018-03-04 */
public class ImdbFromFileServiceImpl extends AbstractImdbService {

    @Override
    public List<TorrentDetail> findTorrents() {
        Set<Object> keySet = ShowsPropertiesHelper.getInstance().keySet();
        Set<Object> imdbIds = keySet.stream()
                .filter(key -> ((String) key).endsWith("imdbId"))
                .collect(Collectors.toSet());
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        for (Object keyObj : imdbIds) {
            String imdbId = shows.getProperty(String.valueOf(keyObj));
            GetTorrentsTask firstTask = new GetTorrentsTask(createUrl(imdbId, 1), executorService);
            executeFirstTask(firstTask, torrentResponses, imdbId);

            List<GetTorrentsTask> tasks = createGetTorrentsTasks(imdbId);
            executeTasks(tasks, torrentResponses);
        }

        List<TorrentDetail> torrentDetails = torrentResponses.stream()
                .flatMap(tr -> tr.getTorrents().stream())
                .collect(Collectors.toList());
        logger.info("[{}] torrent details downloaded.", torrentDetails.size());
        return torrentDetails;
    }
}
