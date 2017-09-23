package pg.factory;

import pg.service.match.MatchByImdbService;
import pg.service.match.MatchService;
import pg.service.match.MatchServiceImpl;
import pg.web.model.ProgramMode;

/**Created by Gawa 2017-09-23*/
public class MatchServiceFactory {

    private MatchServiceFactory() {}

    public static MatchService getMatchService(ProgramMode programMode) {
        switch (programMode) {
            case IMDB:
                return new MatchByImdbService();
            default:
                return new MatchServiceImpl();
        }
    }
}
