package pg.services.match.filter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pg.TorrentDetailBuilder;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HistoryFilterTest {

    private Filter filter;

    @BeforeEach
    public void setUp() {
        filter = new HistoryFilter();
    }

    @Test
    @Disabled
    public void givenTorrentsWhenFilterThenReturnNothing() {
        TorrentDetailBuilder builder = new TorrentDetailBuilder();
        builder.withTitle("The Walking Dead S08E16 720p HDTV x264-AVS EZTV")
                .withSeason("0")
                .withEpisode("0");
        List<TorrentDetail> torrents = Stream.of(builder.mockTorrentDetail()).collect(Collectors.toList());

        List<TorrentDetail> filtered = filter.apply(torrents);

        assertThat(filtered).isEmpty();
    }
}