package pg.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.factory.FilterFactory;
import pg.filter.Filter;
import pg.loader.ShowsPropertiesLoader;
import pg.util.JsonUtils;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.ReducedDetailBuilder;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.*;
import java.util.stream.Collectors;

/**Created by Pawel Gawedzki on 9/21/2017.*/
public class MatchByImdbService implements MatchService {

    private static final Logger logger = LogManager.getLogger(FileServiceImpl.class);

    private List<ReducedDetail> matchingTorrents;

    public MatchByImdbService() {
        matchingTorrents = new LinkedList<>();
    }

    @Override
    public void filterTorrents(List<TorrentResponse> torrents) {
        if (torrents == null || torrents.isEmpty()) {
            return;
        }
        List<TorrentDetail> torrentDetails = torrents.stream()
                .flatMap(torrentResponse -> torrentResponse.getTorrents().stream())
                .collect(Collectors.toList());

        List<TorrentDetail> filtered = new LinkedList<>(torrentDetails);
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

    @Override
    public boolean hasFoundMatchingTorrents() {
        return !matchingTorrents.isEmpty();
    }

    @Override
    public List<ReducedDetail> getMatchingTorrents() {
        return matchingTorrents;
    }
}
