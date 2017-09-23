package pg.service.match;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.loader.ShowsPropertiesLoader;
import pg.web.model.torrent.ReducedDetail;

import java.util.LinkedList;
import java.util.List;

/**Created by Gawa 2017-09-23*/
public abstract class AbstractMatchService implements MatchService {

    protected final Logger logger;
    protected final List<ReducedDetail> matchingTorrents;
    protected final ShowsPropertiesLoader shows;

    public AbstractMatchService() {
        shows = ShowsPropertiesLoader.getInstance();
        matchingTorrents = new LinkedList<>();
        logger = LogManager.getLogger(this.getClass());
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
