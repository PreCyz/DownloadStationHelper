package pg.services.match.filter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pg.TorrentDetailBuilder;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HistoryFilterTest {

    private Filter filter;

    @Before
    public void setUp() {
        filter = new HistoryFilter();
    }

    @Test
    @Ignore
    public void givenTorrentsWhenFilterThenReturnNothing() {
        TorrentDetailBuilder builder = new TorrentDetailBuilder();
        builder.withTitle("The Walking Dead S08E16 720p HDTV x264-AVS EZTV")
                .withSeason("0")
                .withEpisode("0");
        List<TorrentDetail> torrents = Stream.of(builder.mockTorrentDetail()).collect(Collectors.toList());

        List<TorrentDetail> filtered = filter.apply(torrents);

        assertThat(filtered, hasSize(0));
    }
}