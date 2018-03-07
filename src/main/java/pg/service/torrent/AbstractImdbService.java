package pg.service.torrent;

import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.ui.window.controller.task.atomic.GetTorrentsTask;
import pg.util.JsonUtils;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Created by Gawa 2018-03-07 */
abstract class AbstractImdbService extends AbstractTorrentService {

    private int numberOfTorrents = 0;

    AbstractImdbService() {
        super();
    }

    public abstract List<TorrentDetail> findTorrents();

    protected void executeFirstTask(GetTorrentsTask firstTask, List<TorrentResponse> torrentResponses, String imdbId) {
        boolean taskInProgress = true;
        while (taskInProgress) {
            if (firstTask.isDone()) {
                String request = firstTask.getRequestUrl();
                logger.info("Request [{}] finished.", request);
                String response = firstTask.getResponse();
                Optional<TorrentResponse> torrentResponse = JsonUtils.convertFromString(response, TorrentResponse.class);
                if (torrentResponse.isPresent()) {
                    numberOfTorrents = torrentResponse.get().getTorrentsCount();
                    torrentResponses.add(torrentResponse.get());
                    logger.info("[{}] torrents to download for imdb {}.", numberOfTorrents, imdbId);
                    logger.info("[{}] torrents in first response.", torrentResponse.get().getTorrents().size());
                }
                taskInProgress = false;
            }
            if (taskInProgress) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error("Error when sleep thread.", e);
                    throw new ProgramException(UIError.GET_TORRENTS);
                }
            }
        }
    }

    protected String createUrl(String imdbId, int currentPage) {
        String url = super.createUrl(currentPage);
        return String.format("%s&imdb_id=%s", url, imdbId);
    }

    protected List<GetTorrentsTask> createGetTorrentsTasks(String imdbId) {
        Integer numberOfPages = getNumberOfPages();
        List<GetTorrentsTask> tasks = new ArrayList<>(numberOfPages);
        for (int page = defaultPage; page <= numberOfPages; page++) {
            tasks.add(new GetTorrentsTask(createUrl(imdbId, page), executorService));
        }
        return tasks;
    }

    @Override
    protected Integer getNumberOfPages() {
        Double numberOfRequest = Math.ceil(numberOfTorrents / (double) limit);
        defaultPage = 2;
        return numberOfRequest.intValue();
    }
}
