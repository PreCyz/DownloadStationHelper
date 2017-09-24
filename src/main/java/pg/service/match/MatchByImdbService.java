package pg.service.match;

import pg.factory.FilterFactory;
import pg.filter.Filter;
import pg.util.JsonUtils;
import pg.web.model.torrent.ReducedDetailBuilder;
import pg.web.model.torrent.TorrentDetail;

import java.util.LinkedList;
import java.util.List;

/**Created by Pawel Gawedzki on 9/21/2017.*/
public class MatchByImdbService extends AbstractMatchService {

    @Override
    public void filterTorrents(List<TorrentDetail> torrents) {
        if (torrents == null || torrents.isEmpty()) {
            return;
        }

        List<TorrentDetail> filtered = new LinkedList<>(torrents);
        for (Filter filter : FilterFactory.getFilters()) {
            filtered = new LinkedList<>(filter.apply(filtered));
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
