package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.loader.ApplicationPropertiesLoader;
import pg.loader.ShowsPropertiesLoader;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.response.TorrentResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-15*/
public class TorrentServiceImpl implements TorrentService {

    private static final Logger logger = LogManager.getLogger(TorrentServiceImpl.class);

    private final int defaultLimit = 100;
    private final int defaultPage = 1;
    private final String defaultUrl = "https://eztv.ag/api/get-torrents";
    private final ApplicationPropertiesLoader application;
    private final String url;

    public TorrentServiceImpl() {
        this.application = ApplicationPropertiesLoader.getInstance();
        this.url = application.getUrl(defaultUrl);
    }

    @Override
    public List<TorrentResponse> findTorrents() {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        for (int page = defaultPage; page <= application.getPage(defaultPage); page++) {
            String getTorrentUrl = createGetTorrentUrl(page);
            logger.info("Executing request for url {}", getTorrentUrl);
            torrentResponses.addAll(getTorrentsFromResponse(getTorrentUrl));
        }
        logger.info(torrentResponses);
        return torrentResponses;
    }

    private List<TorrentResponse> getTorrentsFromResponse(String requestUrl) {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        GetClient client = new GetClient(requestUrl);
        if (client.get().isPresent()) {
            String json = client.get().get();
            Optional<TorrentResponse> response = JsonUtils.convertFromString(json, TorrentResponse.class);
            response.ifPresent(torrentResponses::add);
        } else {
            logger.info("No response for url {}", requestUrl);
        }
        return torrentResponses;
    }

    @Override
    public List<TorrentResponse> findTorrentsByImdbId() {
        Set<Object> keySet = ShowsPropertiesLoader.getInstance().getShowsProperties().keySet();
        Set<Object> imdbIds = keySet.stream().filter(key -> ((String) key).endsWith("imdbId")).collect(Collectors.toSet());
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        for (Object keyObj : imdbIds) {
            String getTorrentByImdbUrl = createGetTorrentByImdbUrl(String.valueOf(keyObj));
            torrentResponses.addAll(getTorrentsFromResponse(getTorrentByImdbUrl));
        }
        return torrentResponses;
    }

    protected String createGetTorrentUrl(int currentPage) {
        String limit = String.format("limit=%s", application.getLimit(defaultLimit));
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limit, page);
    }

    protected String createGetTorrentByImdbUrl(String imdbId) {
        String imdb = String.format("imdb_id=%s", imdbId);
        return String.format("%s?%s", url, imdb);
    }
}
