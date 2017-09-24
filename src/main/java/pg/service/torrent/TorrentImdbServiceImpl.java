package pg.service.torrent;

import pg.loader.ShowsPropertiesLoader;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-23*/
public class TorrentImdbServiceImpl extends AbstractTorrentService implements TorrentService {

    @Override
    public List<TorrentDetail> findTorrents() {
        Set<Object> keySet = ShowsPropertiesLoader.getInstance().keySet();
        Set<Object> imdbIds = keySet.stream().filter(key -> ((String) key).endsWith("imdbId")).collect(Collectors.toSet());
        List<TorrentResponse> torrentResponses = new LinkedList<>();
        for (Object keyObj : imdbIds) {
            String getTorrentByImdbUrl = createUrl(shows.getProperty(String.valueOf(keyObj)));
            logger.info("Executing request for url {}", getTorrentByImdbUrl);
            torrentResponses.addAll(getTorrentsFromResponse(getTorrentByImdbUrl));
        }
        return torrentResponses.stream()
                .flatMap(torrentResponse -> torrentResponse.getTorrents().stream())
                .collect(Collectors.toList());
    }

    protected String createUrl(String imdbId) {
        String imdb = String.format("imdb_id=%s", imdbId);
        return String.format("%s?%s", url, imdb);
    }
}
