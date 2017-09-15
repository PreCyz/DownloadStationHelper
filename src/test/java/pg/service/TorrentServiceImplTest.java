package pg.service;

import org.junit.Test;
import pg.util.PropertyLoader;

import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**Created by Gawa 2017-09-15*/
public class TorrentServiceImplTest {

    private TorrentServiceImpl torrentService;

    @Test
    public void givenShowsPropertiesWhenBuildPrecisionWordMapThenReturnMap() {
        Properties shows = PropertyLoader.loadShowsProperties();
        torrentService = new TorrentServiceImpl(new Properties(), shows);

        Map<String, Integer> map = torrentService.buildPrecisionWordMap();

        assertThat(map.entrySet(), hasSize(2));
        assertThat(map.get("Game of Thrones,HDTV,720p"), is( equalTo(3)));
        assertThat(map.get("The Strain,HDTV,720p"), is( equalTo(3)));
    }

    @Test
    public void givenApplicationPropertiesWhenPrepareUrlThenReturnURL() {
        Properties application = PropertyLoader.loadApplicationProperties();
        torrentService = new TorrentServiceImpl(application, new Properties());

        String actual = torrentService.prepareTorrentUrl(2);

        assertThat(actual, is( equalTo("https://eztv.ag/api/get-torrents?limit=30&page=2")));
    }

}