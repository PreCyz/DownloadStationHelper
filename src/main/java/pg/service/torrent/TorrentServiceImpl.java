package pg.service.torrent;

import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-15*/
class TorrentServiceImpl extends AbstractTorrentService {

    private final int defaultLimit = 100;
    private final int defaultPage = 1;
    private int limit = defaultLimit;

    @Override
    public List<TorrentDetail> findTorrents() {
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        for (int page = defaultPage; page <= application.getPage(defaultPage); page++) {
            String getTorrentUrl = createUrl(page);
            logger.info("Executing request for url {}", getTorrentUrl);
            torrentResponses.addAll(executeRequest(getTorrentUrl));
        }
        int numberOfTorrents = torrentResponses.stream().mapToInt(tr -> tr.getTorrents().size()).sum();
        logger.info("[{}] torrent details downloaded.", numberOfTorrents);
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
