package pg.service.match;

import pg.util.JsonUtils;
import pg.web.model.ShowKeys;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.ReducedDetailBuilder;
import pg.web.model.torrent.TorrentDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**Created by Gawa on 15/08/17*/
public class MatchServiceImpl extends AbstractMatchService {

    private int matchPrecision;

    public MatchServiceImpl() {
        super();
        matchPrecision = 0;
    }

    @Override
    protected void matchTorrents(List<TorrentDetail> torrents) {
        List<TorrentDetail> filtered = applyFilters(torrents);
        if (filtered.isEmpty()) {
            return;
        }
        Map<String, Integer> map = buildPrecisionWordMap();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            matchPrecision = entry.getValue();
            String baseWord = entry.getKey();
            filtered.forEach(torrentDetail -> {
                Optional<ReducedDetail> priorityOpt = matchTorrent(baseWord, torrentDetail);
                priorityOpt.ifPresent(matchingTorrents::add);
            });
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

    protected Optional<ReducedDetail> matchTorrent(String baseWords, TorrentDetail torrent) {
        String[] words = baseWords.split(",");
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
}
