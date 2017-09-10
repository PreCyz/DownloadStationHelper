package pg.service;

import org.junit.Before;
import org.junit.Test;
import pg.util.JsonUtils;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.TorrentResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**Created by Gawa on 15/08/17.*/
public class SearchServiceTest {

    private SearchService searchService;
    private TorrentResponse torrentResponse;

    @Before
    public void setUp() throws Exception {
        Optional<TorrentResponse> jsonResponse =
                JsonUtils.convertFromFile("testTorrentDetails.json", TorrentResponse.class);
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
        searchService = new SearchService(0);
        searchService.setMatchPrecision(matchPrecision);
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
        searchService = new SearchService(0);
        searchService.setMatchPrecision(matchPrecision);
        List<ReducedDetail> filtered = new LinkedList<>();

        torrentResponse.getTorrents().forEach(torrentDetail ->
            searchService.matchTorrent(word.split(","), torrentDetail).ifPresent(filtered::add)
        );

        assertThat(filtered, hasSize(1));
    }

}