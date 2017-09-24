package pg.service.torrent;

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

/**Created by Gawa 2017-09-23*/
public abstract class AbstractTorrentService implements TorrentService {

    protected final Logger logger;

    private final String defaultUrl = "https://eztv.ag/api/get-torrents";
    protected final ApplicationPropertiesLoader application;
    protected final ShowsPropertiesLoader shows;
    protected final String url;

    public AbstractTorrentService() {
        this.application = ApplicationPropertiesLoader.getInstance();
        this.shows = ShowsPropertiesLoader.getInstance();
        this.url = application.getUrl(defaultUrl);
        this.logger = LogManager.getLogger(this.getClass());
    }

    protected List<TorrentResponse> executeRequest(String requestUrl) {
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
}
