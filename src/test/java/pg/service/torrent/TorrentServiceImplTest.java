package pg.service.torrent;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**Created by Gawa 2017-09-15*/
public class TorrentServiceImplTest {

    private TorrentServiceImpl torrentService;

    @Test
    public void givenShowsPropertiesWhenBuildPrecisionWordMapThenReturnMap() {
        torrentService = new TorrentServiceImpl();

        /*Map<String, Integer> map = torrentService.buildPrecisionWordMap();

        assertThat(map.entrySet(), hasSize(2));
        assertThat(map.get("Game of Thrones,HDTV,720p"), is( equalTo(3)));
        assertThat(map.get("The Strain,HDTV,720p"), is( equalTo(3)));*/
    }

    @Test
    public void givenApplicationPropertiesWhenPrepareUrlThenReturnURL() {
        torrentService = new TorrentServiceImpl();

        String actual = torrentService.createUrl(2);

        assertThat(actual, is( equalTo("https://eztv.ag/api/get-torrents?limit=30&page=2")));
    }

    @Test
    public void matchTorrents() throws Exception {
    }

    @Test
    public void buildPrecisionWordMap() throws Exception {
    }

    @Test
    public void hasFoundMatchingTorrents() throws Exception {
    }

    @Test
    public void getMatchingTorrents() throws Exception {
    }

    @Test
    public void getTorrentResponses() throws Exception {
    }

}