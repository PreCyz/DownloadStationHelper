package pg.service.match;

import org.junit.Before;
import org.junit.Test;
import pg.program.ShowDetail;
import pg.util.JsonUtils;
import pg.web.torrent.ReducedDetail;
import pg.web.torrent.TorrentResponse;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**Created by Gawa on 15/08/17.*/
public class MatchServiceImplTest {
    private MatchServiceImpl matchService;
    private TorrentResponse torrentResponse;

    @Before
    public void setUp() {
        String osName = System.getProperty("os.name");
        URL jsonUrl = getClass().getClassLoader().getResource("testTorrentDetails.json");
        Path jsonPath;
        if (osName.startsWith("Windows")) {
            jsonPath = Paths.get(jsonUrl.getPath().substring(1));
        } else {
            jsonPath = Paths.get(jsonUrl.getPath());
        }
        Optional<TorrentResponse> jsonResponse = JsonUtils.convertFromFile(
                jsonPath,
                TorrentResponse.class);
        this.torrentResponse = jsonResponse.get();
    }

    @Test
    public void whenConvertFromFileThenReturnResponseObject() {
        assertThat(torrentResponse, notNullValue());
    }

    @Test
    public void givenTorrentsDetailWhenSearchThenReturnTorrents() {
        ShowDetail showDetail = new ShowDetail(1, "Stephen Colbert 2017");
        showDetail.setBaseWords("720p,HDTV");
        matchService = new MatchServiceImpl();
        List<ReducedDetail> filtered = new LinkedList<>();

        torrentResponse.getTorrents().forEach(torrentDetail ->
            matchService.matchTorrent(showDetail, torrentDetail).ifPresent(filtered::add)
        );

        assertThat(filtered, hasSize(1));
    }

    @Test
    public void givenTorrentsDetailWhenSearchThenReturnTorrent1() {
        ShowDetail showDetail = new ShowDetail(1, "Cheaters");
        showDetail.setBaseWords("720p");
        matchService = new MatchServiceImpl();
        List<ReducedDetail> filtered = new LinkedList<>();

        torrentResponse.getTorrents().forEach(torrentDetail ->
            matchService.matchTorrent(showDetail, torrentDetail).ifPresent(filtered::add)
        );

        assertThat(filtered, hasSize(1));
    }

    @Test
    public void givenTorrentsDetailAndNoShowDetailWhenMatchTorrentThenReturnNoTorrents() {
        ShowDetail showDetail = new ShowDetail(1, "Some not existing title");
        showDetail.setBaseWords("720p");
        matchService = new MatchServiceImpl();
        List<ReducedDetail> filtered = new LinkedList<>();

        torrentResponse.getTorrents().forEach(torrentDetail ->
                matchService.matchTorrent(showDetail, torrentDetail).ifPresent(filtered::add)
        );

        assertThat(filtered, hasSize(0));
    }

}