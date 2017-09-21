package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.factory.FilterFactory;
import pg.filter.Filter;
import pg.loader.ShowsPropertiesLoader;
import pg.util.JsonUtils;
import pg.web.model.ShowKeys;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.ReducedDetailBuilder;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.*;
import java.util.stream.Collectors;

/**Created by Gawa on 15/08/17*/
public class MatchServiceImpl implements MatchService {

    private static final Logger logger = LogManager.getLogger(FileServiceImpl.class);

    private final Properties shows;
    private int matchPrecision;
    private List<ReducedDetail> matchingTorrents;

    public MatchServiceImpl() {
        this.shows = ShowsPropertiesLoader.getInstance().getShowsProperties();
        this.matchPrecision = 0;
        matchingTorrents = new LinkedList<>();
    }

    private List<ReducedDetail> matchTorrents(final String baseWord, List<TorrentDetail> torrents) {
        if (torrents == null || torrents.isEmpty()) {
            return Collections.emptyList();
        }
        List<TorrentDetail> filtered = new LinkedList<>(torrents);
        for (Filter filter : FilterFactory.getFilters()) {
            filtered = new LinkedList<>(filter.apply(filtered));
        }

        List<ReducedDetail> matchedTorrents = new LinkedList<>();
        filtered.forEach(torrentDetail -> {
            Optional<ReducedDetail> priorityOpt = matchTorrent(baseWord.split(","), torrentDetail);
            priorityOpt.ifPresent(matchedTorrents::add);
        });

        return matchedTorrents;
    }

    protected Optional<ReducedDetail> matchTorrent(String[] words, TorrentDetail torrent) {
        int match = 0;
        for (String word : words) {
            if (torrent.getTitle().contains(word)) {
                match++;
            }
        }
        if (matchPrecision == 0) {
            matchPrecision = words.length;
        }
        if (match >= matchPrecision) {
            return Optional.of(ReducedDetailBuilder.newInstance()
                    .withTitle(torrent.getTitle())
                    .withMagnetUrl(torrent.getMagnetUrl())
                    .withDateReleased(JsonUtils.dateFromLong(torrent.getDateReleased() * 1000))
                    .withMatchPrecision(match)
                    .withTorrentUrl(torrent.getTorrentUrl())
                    .withImdbId(torrent.getImdbId())
                    .withEpisode(torrent.getEpisode())
                    .withSeason(torrent.getSeason())
                    .create());
        }
        return Optional.empty();
    }

    @Override
    public void filterTorrents(List<TorrentResponse> torrents) {
        List<TorrentDetail> torrentDetails = torrents.stream()
                .flatMap(torrentResponse -> torrentResponse.getTorrents().stream())
                .collect(Collectors.toList());
        Map<String, Integer> map = buildPrecisionWordMap();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            matchPrecision = entry.getValue();
            matchingTorrents.addAll(matchTorrents(entry.getKey(), torrentDetails));
        }
        if (hasFoundMatchingTorrents()) {
            logger.info(JsonUtils.convertToString(matchingTorrents));
        } else {
            logger.info("No matching torrents found.");
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
                    Integer matchPrecision = Integer.valueOf(shows.getProperty(precisionKey,
                            String.valueOf(baseWords.split(",").length)));
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
    public boolean hasFoundMatchingTorrents() {
        return !matchingTorrents.isEmpty();
    }

    @Override
    public List<ReducedDetail> getMatchingTorrents() {
        return matchingTorrents;
    }
}
