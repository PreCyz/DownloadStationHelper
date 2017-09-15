package pg.service;

import pg.util.JsonUtils;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.ReducedDetailBuilder;
import pg.web.model.torrent.TorrentDetail;

import java.util.*;
import java.util.stream.Collectors;

/**Created by Gawa on 15/08/17*/
public class SearchServiceImpl implements SearchService {

    //private long timestamp;
    private int matchPrecision;
    private int torrentAge = 0;

    public SearchServiceImpl(int torrentAge) {
        this.matchPrecision = 0;
        this.torrentAge = torrentAge;
    }

    public List<ReducedDetail> search(final String baseWord, List<TorrentDetail> torrents) {
        if (torrents == null || torrents.isEmpty()) {
            return Collections.emptyList();
        }
        List<TorrentDetail> filtered;
        if (torrentAge > 0) {
            filtered = filterByDate(torrents);
        } else {
            filtered = new LinkedList<>(torrents);
        }

        List<ReducedDetail> matchedTorrents = new LinkedList<>();
        filtered.forEach(torrentDetail -> {
            Optional<ReducedDetail> priorityOpt = matchTorrent(baseWord.split(","), torrentDetail);
            priorityOpt.ifPresent(matchedTorrents::add);
        });
        return matchedTorrents;

    }

    protected List<TorrentDetail> filterByDate(List<TorrentDetail> torrents) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -torrentAge);
        final long timestamp = yesterday.getTimeInMillis() / 1000;
        return torrents.stream()
                    .filter(torrent -> torrent.getDateReleased() > timestamp)
                    .collect(Collectors.toList());
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

    public void setMatchPrecision(int matchPrecision) {
        this.matchPrecision = matchPrecision;
    }
}
