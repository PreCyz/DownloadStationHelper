package pg.service.match;

import pg.util.JsonUtils;
import pg.web.model.torrent.ReducedDetailBuilder;
import pg.web.model.torrent.TorrentDetail;

import java.util.List;

/**Created by Pawel Gawedzki on 9/21/2017.*/
public class MatchByImdbService extends AbstractMatchService {

    @Override
    protected void matchTorrents(List<TorrentDetail> torrents) {
        List<TorrentDetail> filtered = applyFilters(torrents);
        if (filtered == null || filtered.isEmpty()) {
            return;
        }

        filtered.forEach(torrent -> matchingTorrents.add(ReducedDetailBuilder.newInstance()
                    .withTitle(torrent.getTitle())
                    .withMagnetUrl(torrent.getMagnetUrl())
                    .withDateReleased(JsonUtils.dateFromLong(torrent.getDateReleased() * 1000))
                    .withTorrentUrl(torrent.getTorrentUrl())
                    .withImdbId(torrent.getImdbId())
                    .withEpisode(torrent.getEpisode())
                    .withSeason(torrent.getSeason())
                    .create())
        );
    }
}
