package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.loader.ShowsPropertiesLoader;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.loader.ApplicationPropertiesLoader;
import pg.util.StringUtils;
import pg.web.model.SettingKeys;
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

    private final int defaultTorrentAge = 0;
    private final ApplicationPropertiesLoader application;
    private final Properties shows;
    private int torrentAge;
    private int matchPrecision;
    private List<ReducedDetail> matchingTorrents;

    public MatchServiceImpl() {
        this.application = ApplicationPropertiesLoader.getInstance();
        this.shows = ShowsPropertiesLoader.getInstance().getShowsProperties();
        this.torrentAge = application.getTorrentAge(defaultTorrentAge);
        this.matchPrecision = 0;
        matchingTorrents = new LinkedList<>();
    }

    private List<ReducedDetail> matchTorrents(final String baseWord, List<TorrentDetail> torrents) {
        if (torrents == null || torrents.isEmpty()) {
            return Collections.emptyList();
        }
        List<TorrentDetail> filtered;
        if (isFilterByDate()) {
            filtered = filterByDate(torrents);
        } else {
            filtered = new LinkedList<>(torrents);
        }
        boolean allowRepeat = StringUtils.booleanFromString(application.getRepeatDownload("false"));
        if (!allowRepeat) {
            filtered = filterByMatchingHistorically(filtered);
        }

        List<ReducedDetail> matchedTorrents = new LinkedList<>();
        filtered.forEach(torrentDetail -> {
            Optional<ReducedDetail> priorityOpt = matchTorrent(baseWord.split(","), torrentDetail);
            priorityOpt.ifPresent(matchedTorrents::add);
        });

        return matchedTorrents;
    }

    private boolean isFilterByDate() {
        return torrentAge > 0;
    }

    protected List<TorrentDetail> filterByDate(List<TorrentDetail> torrents) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -torrentAge);
        final long timestamp = yesterday.getTimeInMillis() / 1000;
        return torrents.stream()
                    .filter(torrent -> torrent.getDateReleased() > timestamp)
                    .collect(Collectors.toList());
    }

    private List<TorrentDetail> filterByMatchingHistorically(List<TorrentDetail> filtered) {
        Optional<Map> mapOpt = JsonUtils.convertFromFile(
                AppConstants.fullFilePath(AppConstants.MATCHING_TORRENTS_FILE),
                Map.class
        );
        if (mapOpt.isPresent()) {
            return filtered.stream()
                    .filter(torrentDetail -> !mapOpt.get().containsKey(torrentDetail.getTitle()))
                    .collect(Collectors.toList());
        }
        return filtered;
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
        return matchingTorrents != null && !matchingTorrents.isEmpty();
    }

    @Override
    public List<ReducedDetail> getMatchingTorrents() {
        return matchingTorrents;
    }
}
