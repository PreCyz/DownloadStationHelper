package pg.service.torrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.props.ApplicationPropertiesHelper;
import pg.props.ShowsPropertiesHelper;
import pg.ui.window.controller.task.atomic.GetTorrentsTask;
import pg.util.JsonUtils;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**Created by Gawa 2017-09-23*/
abstract class AbstractTorrentService implements TorrentService {

    final String defaultUrl = "https://eztv.ag/api/get-torrents";
    final int defaultLimit = 100;

    protected final Logger logger;
    protected int defaultPage = 1;
    protected final ApplicationPropertiesHelper application;
    protected final ShowsPropertiesHelper shows;
    protected final String url;
    protected int limit = defaultLimit;
    protected ExecutorService executorService;

    protected AbstractTorrentService() {
        this.application = ApplicationPropertiesHelper.getInstance();
        this.shows = ShowsPropertiesHelper.getInstance();
        this.url = application.getUrl(defaultUrl);
        this.logger = LogManager.getLogger(this.getClass());
        this.executorService = Executors.newFixedThreadPool(application.getPage(defaultPage));
    }

    @Override
    public abstract List<TorrentDetail> findTorrents();

    protected Integer getNumberOfPages() {
        return application.getPage(defaultPage);
    }

    protected String createUrl(int currentPage) {
        limit = application.getLimit(defaultLimit);
        String limitParam = String.format("limit=%d", limit);
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limitParam, page);
    }

    protected List<GetTorrentsTask> createGetTorrentsTasks() {
        Integer numberOfPages = getNumberOfPages();
        List<GetTorrentsTask> tasks = new ArrayList<>(numberOfPages);
        for (int page = defaultPage; page <= numberOfPages; page++) {
            tasks.add(new GetTorrentsTask(createUrl(page), executorService));
        }
        return tasks;
    }

    protected void executeTasks(List<GetTorrentsTask> tasks, List<TorrentResponse> torrentResponses) {
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
