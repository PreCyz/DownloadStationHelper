package pg.service.torrent;

import pg.web.response.TorrentResponse;

import java.util.LinkedList;
import java.util.List;

/**Created by Gawa 2017-09-15*/
public class TorrentServiceImpl extends AbstractTorrentService {

    private final int defaultLimit = 100;
    private final int defaultPage = 1;

    @Override
    public List<TorrentResponse> findTorrents() {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        for (int page = defaultPage; page <= application.getPage(defaultPage); page++) {
            String getTorrentUrl = createUrl(page);
            logger.info("Executing request for url {}", getTorrentUrl);
            torrentResponses.addAll(getTorrentsFromResponse(getTorrentUrl));
        }
        logger.info(torrentResponses);
        return torrentResponses;
    }

    protected String createUrl(int currentPage) {
        String limit = String.format("limit=%s", application.getLimit(defaultLimit));
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limit, page);
    }
}
