package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.JsonUtils;
import pg.loader.ApplicationPropertiesLoader;
import pg.web.client.GetClient;
import pg.web.model.SettingKeys;
import pg.web.response.TorrentResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**Created by Gawa 2017-09-15*/
public class TorrentServiceImpl implements TorrentService {

    private static final Logger logger = LogManager.getLogger(TorrentServiceImpl.class);

    private final int defaultLimit = 100;
    private final int defaultPage = 1;
    private final String defaultUrl = "https://eztv.ag/api/get-torrents";
    private final ApplicationPropertiesLoader application;

    public TorrentServiceImpl() {
        this.application = ApplicationPropertiesLoader.getInstance();
    }

    @Override
    public List<TorrentResponse> findTorrents() {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        for (int page = defaultPage; page <= application.getPage(defaultPage); page++) {
            String url = createGetTorrentUrl(page);
            logger.info("Executing request for url {}", url);
            GetClient client = new GetClient(url);
            if (client.get().isPresent()) {
                String json = client.get().get();
                Optional<TorrentResponse> response = JsonUtils.convertFromString(json, TorrentResponse.class);
                response.ifPresent(torrentResponses::add);
            } else {
                logger.info("No response for url {}", url);
            }
        }
        logger.info(torrentResponses);
        return torrentResponses;
    }

    protected String createGetTorrentUrl(int currentPage) {
        String url = application.getUrl(defaultUrl);
        String limit = String.format("limit=%s", application.getLimit(defaultLimit));
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limit, page);
    }
}
