package pg.service;

import org.junit.Before;
import org.junit.Test;
import pg.util.JsonUtils;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.TorrentResponse;

import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**Created by Gawa on 15/08/17.*/
public class MatchServiceImplTest {
    @Test
    public void search() throws Exception {
    }

    @Test
    public void filterByDate() throws Exception {
    }

    @Test
    public void matchTorrent() throws Exception {
    }

    @Test
    public void setMatchPrecision() throws Exception {
    }

    private MatchServiceImpl searchService;
    private TorrentResponse torrentResponse;

    @Before
    public void setUp() throws Exception {
        URL jsonUrl = getClass().getClassLoader().getResource("testTorrentDetails.json");
        Optional<TorrentResponse> jsonResponse = JsonUtils.convertFromFile(
                Paths.get(jsonUrl.getPath()),
                TorrentResponse.class);
        this.torrentResponse = jsonResponse.get();
    }

    @Test
    public void whenConvertFromFileThenReturnResponseObject() {
        assertThat(torrentResponse, notNullValue());
    }

    @Test
    public void givenTorrentsDetailWhenSearchThenReturnTorrents() {
        int matchPrecision = 3;
        final String word = "Stephen Colbert 2017 08 14 Anthony Scaramucci,720p,HDTV";
        searchService = new MatchServiceImpl();
        List<ReducedDetail> filtered = new LinkedList<>();

        torrentResponse.getTorrents().forEach(torrentDetail ->
            searchService.matchTorrent(word.split(","), torrentDetail).ifPresent(filtered::add)
        );

        assertThat(filtered, hasSize(1));
    }

    @Test
    public void givenTorrentsDetailWhenSearchThenReturnTorrents2() {
        int matchPrecision = 2;
        final String word = "Cheaters,720p";
        searchService = new MatchServiceImpl();
        List<ReducedDetail> filtered = new LinkedList<>();

        torrentResponse.getTorrents().forEach(torrentDetail ->
            searchService.matchTorrent(word.split(","), torrentDetail).ifPresent(filtered::add)
        );

        assertThat(filtered, hasSize(1));
    }

}