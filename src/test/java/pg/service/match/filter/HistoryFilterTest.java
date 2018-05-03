package pg.service.match.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HistoryFilterTest {

    private Filter filter;

    @Before
    public void setUp() {
        filter = new HistoryFilter();
    }

    @Test
    public void givenTorrentsWhenFilterThenReturnNothing() {
        TorrentDetail torrentDetail = mock(TorrentDetail.class);
        when(torrentDetail.getTitle()).thenReturn("The Walking Dead S08E16 720p HDTV x264-AVS EZTV");
        when(torrentDetail.getSeason()).thenReturn("0");
        when(torrentDetail.getEpisode()).thenReturn("0");
        List<TorrentDetail> torrents = Stream.of(torrentDetail).collect(Collectors.toList());

        List<TorrentDetail> filtered = filter.apply(torrents);

        assertThat(filtered, hasSize(0));
    }
}