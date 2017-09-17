package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.JsonUtils;
import pg.util.PropertyLoader;
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
    private final Properties application;

    public TorrentServiceImpl() {
        this.application = PropertyLoader.getApplicationProperties();
    }

    @Override
    public List<TorrentResponse> findTorrents() {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        int pages = Integer.valueOf(application.getProperty(SettingKeys.PAGE.key(), String.valueOf(defaultPage)));
        for (int page = defaultPage; page <= pages; page++) {
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
        String url = application.getProperty(SettingKeys.URL.key(), defaultUrl);
        String limit = String.format("limit=%s", application.getProperty(SettingKeys.LIMIT.key(),
                String.valueOf(defaultLimit)));
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limit, page);
    }
}
