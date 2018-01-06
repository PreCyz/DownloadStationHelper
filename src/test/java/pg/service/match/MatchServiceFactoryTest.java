package pg.service.match;

import org.junit.Test;
import pg.program.ProgramMode;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**Created by Gawa 2017-10-02*/
public class MatchServiceFactoryTest {

    @Test
    public void givenModeAllWhenGetMatchServiceTheReturnMatchServiceImpl() {
        MatchService matchService = MatchServiceFactory.getMatchService(ProgramMode.ALL);

        assertThat(matchService, is( instanceOf(MatchServiceImpl.class)));
    }

    @Test
    public void givenModeImdbWhenGetMatchServiceTheReturnMatchServiceImpl() {
        MatchService matchService = MatchServiceFactory.getMatchService(ProgramMode.IMDB);

        assertThat(matchService, is( instanceOf(MatchByImdbService.class)));
    }

}