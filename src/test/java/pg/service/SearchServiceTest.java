package pg.service;

import org.junit.Before;
import org.junit.Test;
import pg.util.JsonUtils;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.Calendar;
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
        searchService = new SearchService();
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
        searchService = new SearchService();
        searchService.setMatchPrecision(matchPrecision);
        List<ReducedDetail> filtered = new LinkedList<>();

        torrentResponse.getTorrents().forEach(torrentDetail ->
            searchService.matchTorrent(word.split(","), torrentDetail).ifPresent(filtered::add)
        );

        assertThat(filtered, hasSize(1));
    }

    @Test
    public void givenTimestampWhenFilterByDateThenReturnProperTorrents() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.AUGUST, 15, 13, 0, 0);
        searchService = new SearchService();
        searchService.setTimestamp(calendar.getTimeInMillis() / 1000L);

        List<TorrentDetail> actual = searchService.filterByDate(torrentResponse.getTorrents());

        assertThat(actual, hasSize(6));
    }

}