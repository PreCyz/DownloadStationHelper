package pg.service.match;

import pg.program.ShowDetail;
import pg.util.JsonUtils;
import pg.web.torrent.ReducedDetail;
import pg.web.torrent.ReducedDetailBuilder;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.Optional;

/**Created by Gawa on 15/08/17*/
class MatchServiceImpl extends AbstractMatchService {

    public MatchServiceImpl() {
        super();
    }

    @Override
    protected void matchTorrents(List<TorrentDetail> torrents) {
        List<TorrentDetail> filtered = applyFilters(torrents);
        if (!filtered.isEmpty()) {
            for (ShowDetail showDetail : showHelper.getShowDetails()) {
                for (TorrentDetail torrentDetail : filtered) {
                    matchTorrent(showDetail, torrentDetail).ifPresent(matchingTorrents::add);
                }
            }
        }
    }

    protected Optional<ReducedDetail> matchTorrent(ShowDetail showDetail, TorrentDetail torrent) {
        if (torrent.getTitle().startsWith(showDetail.getTitle())) {
            String[] words = showDetail.getBaseWords().split(",");

            int matchCounter = calculateMatch(torrent, words);

            int matchPrecision = getMatchPrecision(showDetail);

            if (matchCounter >= matchPrecision) {
                return Optional.of(ReducedDetailBuilder.newInstance()
                        .withTitle(torrent.getTitle())
                        .withMagnetUrl(torrent.getMagnetUrl())
                        .withDateReleased(JsonUtils.dateFromLong(torrent.getDateReleased() * 1000))
                        .withMatchPrecision(matchCounter)
                        .withTorrentUrl(torrent.getTorrentUrl())
                        .withImdbId(torrent.getImdbId())
                        .withEpisode(torrent.getEpisode())
                        .withSeason(torrent.getSeason())
                        .create());
            }
        }
        return Optional.empty();
    }

    private int calculateMatch(TorrentDetail torrent, String[] words) {
        int match = 0;
        for (String word : words) {
            if (torrent.getTitle().contains(word)) {
                match++;
            }
        }
        return match;
    }

    private int getMatchPrecision(ShowDetail showDetail) {
        int matchPrecision = showDetail.getMatchPrecision();
        if (matchPrecision == 0) {
            matchPrecision = showDetail.getBaseWordsCount();
        }
        return matchPrecision;
    }

}
