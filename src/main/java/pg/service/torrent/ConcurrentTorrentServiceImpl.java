package pg.service.torrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.window.controller.task.atomic.GetTorrentsTask;
import pg.util.JsonUtils;
import pg.web.model.torrent.TorrentDetail;
import pg.web.model.torrent.TorrentResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/** Created by Gawa 2017-10-25 */
public class ConcurrentTorrentServiceImpl implements TorrentService {

    private static final Logger logger = LogManager.getLogger(ConcurrentTorrentServiceImpl.class);

    private final int defaultLimit = 100;
    private final int defaultPage = 1;
    private final String defaultUrl = "https://eztv.ag/api/get-torrents";
    private int limit = defaultLimit;
    private final ExecutorService executorService;
    private final ApplicationPropertiesHelper application;
    private final String url;

    public ConcurrentTorrentServiceImpl() {
        this.application = ApplicationPropertiesHelper.getInstance();
        this.executorService = Executors.newFixedThreadPool(application.getPage(defaultPage));
        this.url = application.getUrl(defaultUrl);
    }

    @Override
    public List<TorrentDetail> findTorrents() {
        Integer numberOfPages = application.getPage(defaultPage);
        List<GetTorrentsTask> tasks = new ArrayList<>(numberOfPages);
        for (int page = defaultPage; page <= numberOfPages; page++) {
            tasks.add(new GetTorrentsTask(createUrl(page), executorService));
        }

        List<TorrentResponse> torrentResponses = new ArrayList<>();
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

    protected String createUrl(int currentPage) {
        limit = application.getLimit(defaultLimit);
        String limitParam = String.format("limit=%d", limit);
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limitParam, page);
    }
}
