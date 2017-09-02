package pg.service;

import pg.util.JsonUtils;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

import java.util.*;
import java.util.stream.Collectors;

/**Created by Gawa on 15/08/17*/
public class SearchService {

    private long timestamp;
    private boolean shouldRestAgain = false;
    private int matchPrecision;

    public SearchService() {
        this.matchPrecision = 0;
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        timestamp = yesterday.getTimeInMillis() / 1000;
    }

    public List<ReducedDetail> search(final String baseWord, List<TorrentDetail> torrents) {
        if (torrents == null || torrents.isEmpty()) {
            return Collections.emptyList();
        }
        List<TorrentDetail> filtered = filterByDate(torrents);

        shouldRestAgain = filtered.size() == torrents.size();
        List<ReducedDetail> matchedTorrents = new LinkedList<>();
        filtered.forEach(torrentDetail -> {
            Optional<ReducedDetail> priorityOpt = matchTorrent(baseWord.split(","), torrentDetail);
            priorityOpt.ifPresent(matchedTorrents::add);
        });
        return matchedTorrents;

    }

    protected List<TorrentDetail> filterByDate(List<TorrentDetail> torrents) {
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
            return Optional.of(new ReducedDetail(
                    torrent.getTitle(),
                    torrent.getMagnetUrl(),
                    JsonUtils.dateFromLong(torrent.getDateReleased() * 1000),
                    match,
                    torrent.getTorrentUrl()));
        }
        return Optional.empty();
    }

    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean shouldRestAgain() {
        return shouldRestAgain;
    }

    public void setMatchPrecision(int matchPrecision) {
        this.matchPrecision = matchPrecision;
    }
}
