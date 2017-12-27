package pg.service.match;

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
