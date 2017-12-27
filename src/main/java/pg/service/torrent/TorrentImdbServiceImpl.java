package pg.service.torrent;

import pg.props.ShowsPropertiesHelper;
import pg.util.StringUtils;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-23*/
class TorrentImdbServiceImpl extends AbstractTorrentService implements TorrentService {
    private String imdbId;

    @Override
    public List<TorrentDetail> findTorrents() {
        Set<Object> keySet = ShowsPropertiesHelper.getInstance().keySet();
        Set<Object> imdbIds = keySet.stream()
                .filter(key -> ((String) key).endsWith("imdbId"))
                .collect(Collectors.toSet());
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        for (Object keyObj : imdbIds) {
            if (StringUtils.nullOrTrimEmpty(imdbId)) {
                imdbId = shows.getProperty(String.valueOf(keyObj));
            }
            String torrentByImdbUrl = createUrl(imdbId);
            logger.info("Executing request for url {}", torrentByImdbUrl);
            torrentResponses.addAll(executeRequest(torrentByImdbUrl));
        }
        List<TorrentDetail> torrentDetails = torrentResponses.stream()
                .flatMap(tr -> tr.getTorrents().stream())
                .collect(Collectors.toList());
        logger.info("[{}] torrent details downloaded.", torrentDetails.size());
        return torrentDetails;
    }

    protected String createUrl(String imdbId) {
        String imdb = String.format("imdb_id=%s", imdbId);
        return String.format("%s?%s", url, imdb);
    }

    public TorrentService withImdbId(String imdbId) {
        this.imdbId = imdbId;
        return this;
    }
}
