package pg.services.torrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.window.controller.completable.UpdatableTask;
import pg.ui.window.controller.task.atomic.call.response.PairResponse;
import pg.ui.window.controller.task.atomic.call.torrent.GetTorrentsCustomCall;
import pg.util.JsonUtils;
import pg.web.torrent.TorrentResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public abstract class AbstractTorrentService {

    final String defaultUrl = "https://eztv.ag/api/get-torrents";
    final int defaultLimit = 100;

    protected int defaultPage = 1;
    protected int limit = defaultLimit;

    private final UpdatableTask<?> fxTask;
    protected final Logger logger;
    protected final ApplicationPropertiesHelper application;
    protected final String url;

    protected AbstractTorrentService(UpdatableTask<?> fxTask) {
        this.fxTask = fxTask;
        this.application = ApplicationPropertiesHelper.getInstance();
        this.url = application.getUrl(defaultUrl);
        this.logger = LogManager.getLogger(this.getClass());
    }

    protected String createUrl(int currentPage) {
        limit = application.getLimit(defaultLimit);
        String limitParam = String.format("limit=%d", limit);
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limitParam, page);
    }

    protected List<TorrentResponse> executeTasks(List<GetTorrentsCustomCall> tasks) {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        if (tasks.isEmpty()) {
            fxTask.updateProgressTo30(30);
            return torrentResponses;
        }
        double onePercentOfProgress = 1.0 / 30;
        fxTask.updateProgressTo30(onePercentOfProgress);

        ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
        CompletionService<PairResponse> ecs = new ExecutorCompletionService<>(executor);
        tasks.forEach(ecs::submit);

        int numberOfCalls = tasks.size();
        for (int i = 0; i < numberOfCalls; i++) {
            try {
                PairResponse response = ecs.take().get();
                logger.info("Request [{}] finished.", response.getUrl());
                List<TorrentResponse> torrentsForRequest = new ArrayList<>(limit);
                JsonUtils.convertFromString(response.getResponse(), TorrentResponse.class)
                        .ifPresent(torrentsForRequest::add);
                logger.info("[{}] torrents in response.",
                        torrentsForRequest.stream()
                                .mapToInt(tr -> tr.getTorrents().size())
                                .sum()
                );
                torrentResponses.addAll(torrentsForRequest);
                double progress = (i + 1d) / getNumberOfPages();
                fxTask.updateProgressTo30(progress);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error when getting torrents.", e);
            }
        }
        return torrentResponses;
    }

    protected abstract Integer getNumberOfPages();
}
