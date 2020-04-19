package pg.services.match;

import pg.program.ProgramMode;

/**Created by Gawa 2017-09-23*/
public class MatchServiceFactory {

    private MatchServiceFactory() {}

    public static MatchService getMatchService(ProgramMode programMode) {
        switch (programMode) {
            case IMDB:
                return new MatchByImdbService();
            case IMDB_COMAND_LINE:
                return new MatchByImdbService();
            default:
                return new MatchServiceImpl();
        }
    }
}
