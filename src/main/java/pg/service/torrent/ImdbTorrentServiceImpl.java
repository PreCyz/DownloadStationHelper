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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-23*/
public class ImdbTorrentServiceImpl extends AbstractImdbService {
    private final String imdbId;
    private final UpdatableTask<?> fxTask;

    public ImdbTorrentServiceImpl(String imdbId, UpdatableTask<?> fxTask) {
        super();
        this.imdbId = imdbId;
        this.fxTask = fxTask;
    }

    @Override
    public List<TorrentDetail> findTorrents() {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        GetTorrentsTask firstTask = new GetTorrentsTask(createUrl(imdbId, 1), executorService);
        executeFirstTask(firstTask, torrentResponses, imdbId);
        executorService = Executors.newFixedThreadPool(getNumberOfPages() - 1);

        List<GetTorrentsTask> tasks = createGetTorrentsTasks(imdbId);
        executeTasks(tasks, torrentResponses);
        List<TorrentDetail> torrentDetails = torrentResponses.stream()
                .flatMap(tr -> tr.getTorrents().stream())
                .collect(Collectors.toList());
        logger.info("[{}] torrent details downloaded.", torrentDetails.size());
        return torrentDetails;
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
