package pg.service.torrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class TorrentServiceWithTaskImpl extends ConcurrentTorrentServiceImpl {

    private static final Logger logger = LogManager.getLogger(TorrentServiceWithTaskImpl.class);

    private UpdatableTask<?> fxTask;

    TorrentServiceWithTaskImpl(UpdatableTask<?> fxTask) {
        super();
        this.fxTask = fxTask;
    }

    @Override
    public List<TorrentDetail> findTorrents() {
        Integer numberOfPages = getPage();
        List<GetTorrentsTask> tasks = new ArrayList<>(numberOfPages);
        for (int page = defaultPage; page <= numberOfPages; page++) {
            tasks.add(new GetTorrentsTask(createUrl(page), executorService));
        }

        List<TorrentResponse> torrentResponses = new ArrayList<>();
        fxTask.updateProgressTo30(0.03);
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
                    logger.info("[{}] torrents in ds.",
                            torrentsForRequest.stream()
                                    .mapToInt(tr -> tr.getTorrents().size())
                                    .sum()
                    );
                    torrentResponses.addAll(torrentsForRequest);
                    fxTask.updateProgressTo30(++done / numberOfPages);
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
        return torrentResponses.stream()
                .flatMap(tr -> tr.getTorrents().stream())
                .collect(Collectors.toList());
    }
}
