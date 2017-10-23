package pg.service.match;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.factory.FilterFactory;
import pg.filter.Filter;
import pg.props.ShowsPropertiesHelper;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**Created by Gawa 2017-09-23*/
public abstract class AbstractMatchService implements MatchService {

    protected final Logger logger;
    protected final List<ReducedDetail> matchingTorrents;
    protected final ShowsPropertiesHelper shows;

    public AbstractMatchService() {
        shows = ShowsPropertiesHelper.getInstance();
        matchingTorrents = new LinkedList<>();
        logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public void match(List<TorrentDetail> torrents) {
        matchTorrents(torrents);
        logMatchedTorrents();
    }

    @Override
    public boolean hasFoundMatchingTorrents() {
        return !matchingTorrents.isEmpty();
    }

    @Override
    public List<ReducedDetail> getMatchingTorrents() {
        return matchingTorrents;
    }

    protected List<TorrentDetail> applyFilters(List<TorrentDetail> torrents) {
        if (torrents == null || torrents.isEmpty()) {
            return Collections.emptyList();
        }
        List<TorrentDetail> filtered = new LinkedList<>(torrents);
        for (Filter filter : FilterFactory.getFilters()) {
            filtered = new LinkedList<>(filter.apply(filtered));
        }
        return filtered;
    }

    protected void logMatchedTorrents() {
        if (hasFoundMatchingTorrents()) {
            matchingTorrents.stream()
                    .map(ReducedDetail::getTitle)
                    .forEach(title -> logger.info("Match torrent {}", title));
        }
    }

    protected abstract void matchTorrents(List<TorrentDetail> torrents);
}
