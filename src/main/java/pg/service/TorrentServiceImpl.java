package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.SettingKeys;
import pg.web.model.ShowKeys;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.*;
import java.util.stream.Collectors;

/**Created by Gawa 2017-09-15*/
public class TorrentServiceImpl implements TorrentService {

    private static final Logger logger = LogManager.getLogger(TorrentServiceImpl.class);

    private final int defaultLimit = 100;
    private final int defaultPage = 1;
    private final int defaultTorrentAge = 0;
    private final String defaultUrl = "https://eztv.ag/api/get-torrents";
    private final Properties application;
    private final Properties shows;
    private SearchService searchService;
    private List<ReducedDetail> foundTorrents;
    private List<TorrentResponse> torrentResponses;

    public TorrentServiceImpl(Properties application, Properties shows) {
        this.application = application;
        this.shows = shows;
        searchService = new SearchServiceImpl(Integer.valueOf(
                application.getProperty(SettingKeys.TORRENT_AGE.key(), String.valueOf(defaultTorrentAge))
        ));
        foundTorrents = new LinkedList<>();
        torrentResponses = new LinkedList<>();
    }

    @Override
    public void findTorrents() {
        int pages = Integer.valueOf(application.getProperty(SettingKeys.PAGE.key(), String.valueOf(defaultPage)));
        for (int page = defaultPage; page <= pages; page++) {
            String url = prepareTorrentUrl(page);
            logger.info("Executing request for url {}", url);
            GetClient client = new GetClient(url);
            if (client.get().isPresent()) {
                String json = client.get().get();
                Optional<TorrentResponse> response = JsonUtils.convertFromString(json, TorrentResponse.class);
                response.ifPresent(torrentResponse -> torrentResponses.add(torrentResponse));
            } else {
                logger.info("No response for url {}", url);
            }
        }
        logger.info(torrentResponses);
    }

    protected String prepareTorrentUrl(int currentPage) {
        String url = application.getProperty(SettingKeys.URL.key(), defaultUrl);
        String limit = String.format("limit=%s", application.getProperty(SettingKeys.LIMIT.key(),
                String.valueOf(defaultLimit)));
        String page = String.format("page=%d", currentPage);
        return String.format("%s?%s&%s", url, limit, page);
    }

    @Override
    public void matchTorrents() {
        List<TorrentDetail> torrentDetails = torrentResponses.stream()
                .flatMap(torrentResponse -> torrentResponse.getTorrents().stream())
                .collect(Collectors.toList());
        Map<String, Integer> map = buildPrecisionWordMap();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            searchService.setMatchPrecision(entry.getValue());
            foundTorrents.addAll(searchService.search(entry.getKey(), torrentDetails));
        }
    }

    protected Map<String, Integer> buildPrecisionWordMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Object keyObject : shows.keySet()) {
            String key = (String) keyObject;
            if (key.endsWith(ShowKeys.baseWords.name())) {
                String baseWords = shows.getProperty(key);
                if (baseWords != null && baseWords.trim().length() > 0) {
                    String precisionKey = key.substring(0, key.indexOf(ShowKeys.baseWords.name())) +
                            ShowKeys.matchPrecision.name();
                    String precision = shows.getProperty(precisionKey,
                            String.valueOf(baseWords.split(",").length));
                    Integer matchPrecision = Integer.valueOf(precision);
                    map.put(baseWords, matchPrecision);
                }
            }
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException("No shows were specified. Add some base words to shows.properties.");
        }
        return map;
    }

    @Override
    public boolean hasFoundTorrents() {
        return foundTorrents != null && !foundTorrents.isEmpty();
    }

    @Override
    public List<ReducedDetail> getFoundTorrents() {
        return foundTorrents;
    }

    @Override
    public List<TorrentResponse> getTorrentResponses() {
        return torrentResponses;
    }
}
