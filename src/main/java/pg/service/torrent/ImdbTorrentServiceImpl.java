package pg.service.torrent;

import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.ui.window.controller.completable.UpdatableTask;
import pg.ui.window.controller.task.atomic.call.response.PairResponse;
import pg.ui.window.controller.task.atomic.call.torrent.GetTorrentsCustomCall;
import pg.util.JsonUtils;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-23*/
public class ImdbTorrentServiceImpl extends AbstractTorrentService implements TorrentService {
    private final String imdbId;
    private int numberOfTorrents = 0;

    public ImdbTorrentServiceImpl(String imdbId, UpdatableTask<?> fxTask) {
        super(fxTask);
        this.imdbId = imdbId;
    }

    @Override
    public List<TorrentDetail> findTorrents() {
        GetTorrentsCustomCall firstTask = new GetTorrentsCustomCall(createUrl(imdbId, 1));
        List<TorrentResponse> torrentResponses = executeFirstTask(firstTask, imdbId);

        List<GetTorrentsCustomCall> tasks = createGetTorrentsTasks(imdbId);
        torrentResponses.addAll(executeTasks(tasks));
        List<TorrentDetail> torrentDetails = torrentResponses.stream()
                .flatMap(tr -> tr.getTorrents().stream())
                .collect(Collectors.toList());
        logger.info("[{}] torrent details downloaded.", torrentDetails.size());
        return torrentDetails;
    }

    protected String createUrl(String imdbId, int currentPage) {
        String requestUrl = createUrl(currentPage);
        return String.format("%s&imdb_id=%s", requestUrl, imdbId);
    }

    protected List<GetTorrentsCustomCall> createGetTorrentsTasks(String imdbId) {
        Integer numberOfPages = getNumberOfPages();
        List<GetTorrentsCustomCall> tasks = new ArrayList<>(numberOfPages);
        for (int page = defaultPage; page <= numberOfPages; page++) {
            tasks.add(new GetTorrentsCustomCall(createUrl(imdbId, page)));
        }
        return tasks;
    }

    protected List<TorrentResponse> executeFirstTask(GetTorrentsCustomCall firstTask, String imdbId) {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            PairResponse response = executorService.submit(firstTask).get();
            logger.info("Request [{}] finished.", response.getUrl());
            Optional<TorrentResponse> torrentResponse =
                    JsonUtils.convertFromString(response.getResponse(), TorrentResponse.class);
            if (torrentResponse.isPresent()) {
                numberOfTorrents = torrentResponse.get().getTorrentsCount();
                torrentResponses.add(torrentResponse.get());
                logger.info("[{}] torrents to download for imdb {}.", numberOfTorrents, imdbId);
                logger.info("[{}] torrents in first response.", torrentResponse.get().getTorrents().size());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error when sleep thread.", e);
            throw new ProgramException(UIError.GET_TORRENTS);
        }

        return torrentResponses;
    }

    @Override
    protected Integer getNumberOfPages() {
        Double numberOfRequest = Math.ceil(numberOfTorrents / (double) limit);
        defaultPage = 2;
        return numberOfRequest.intValue();
    }
}
