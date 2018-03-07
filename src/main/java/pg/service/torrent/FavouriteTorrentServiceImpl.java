package pg.service.torrent;

import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.ui.window.controller.completable.UpdatableTask;
import pg.ui.window.controller.task.atomic.GetTorrentsTask;
import pg.util.JsonUtils;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/** Created by Gawa 2018-01-06 */
public class FavouriteTorrentServiceImpl extends AbstractTorrentService {

    private UpdatableTask<?> fxTask;

    public FavouriteTorrentServiceImpl(UpdatableTask<?> fxTask) {
        super();
        this.fxTask = fxTask;
    }

    @Override
    public List<TorrentDetail> findTorrents() {
        List<GetTorrentsTask> tasks = createGetTorrentsTasks();
        List<TorrentResponse> torrentResponses = new ArrayList<>();
        executeTasks(tasks, torrentResponses);
        return torrentResponses.stream()
                .flatMap(tr -> tr.getTorrents().stream())
                .collect(Collectors.toList());
    }

    @Override
    protected void executeTasks(List<GetTorrentsTask> tasks, List<TorrentResponse> torrentResponses) {
        double onePercentOfProgress = 1.0 / 30;
        fxTask.updateProgressTo30(onePercentOfProgress);
        double done = 0;
        while (!tasks.isEmpty()) {
            for (Iterator<GetTorrentsTask> it = tasks.iterator(); it.hasNext(); ) {
                GetTorrentsTask task = it.next();
                if (task.isDone()) {
                    String request = task.getRequestUrl();
                    logger.info("Request [{}] finished.", request);
                    List<TorrentResponse> torrentsForRequest = new ArrayList<>(limit);
                    String response = task.getResponse();
                    JsonUtils.convertFromString(response, TorrentResponse.class).ifPresent(torrentsForRequest::add);
                    it.remove();
                    logger.info("[{}] torrents in response.",
                            torrentsForRequest.stream()
                                    .mapToInt(tr -> tr.getTorrents().size())
                                    .sum()
                    );
                    torrentResponses.addAll(torrentsForRequest);
                    fxTask.updateProgressTo30(++done / getNumberOfPages());
                }
            }
            if (!tasks.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error("Error when sleep thread.", e);
                    throw new ProgramException(UIError.GET_TORRENTS);
                }
            }
        }
    }
}
