package pg.services.match;

import org.junit.jupiter.api.Test;
import pg.program.ProgramMode;

import static org.assertj.core.api.Assertions.assertThat;

/**Created by Gawa 2017-10-02*/
public class MatchServiceFactoryTest {

    @Test
    public void givenModeAllWhenGetMatchServiceTheReturnMatchServiceImpl() {
        MatchService matchService = MatchServiceFactory.getMatchService(ProgramMode.ALL);
        assertThat(matchService).isInstanceOf(MatchServiceImpl.class);
    }

    @Test
    public void givenModeImdbWhenGetMatchServiceTheReturnMatchServiceImpl() {
        MatchService matchService = MatchServiceFactory.getMatchService(ProgramMode.IMDB);
        assertThat(matchService).isInstanceOf(MatchByImdbService.class);
    }

}