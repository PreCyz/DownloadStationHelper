package pg.service.match.filter;

import org.junit.After;
import org.junit.Test;
import pg.web.torrent.TorrentDetail;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**Created by Gawa 2017-09-24*/
public class ReleaseDateFilterTest {
    private Filter filter;

    private final SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    @After
    public void tearDown() throws Exception {
        filter = null;
    }

    @Test
    public void givenNullReleaseDateWhenApplyThenDontUseFilter() {
        filter = new ReleaseDateFilter(null);
        List<TorrentDetail> torrents = Stream.of(new TorrentDetail()).collect(Collectors.toList());

        List<TorrentDetail> actual = filter.apply(torrents);

        assertThat(actual, hasSize(torrents.size()));
    }

    @Test
    public void givenEmptyReleaseDateWhenApplyThenDontUseFilter() {
        filter = new ReleaseDateFilter("");
        List<TorrentDetail> torrents = Stream.of(new TorrentDetail()).collect(Collectors.toList());

        List<TorrentDetail> actual = filter.apply(torrents);

        assertThat(actual, hasSize(torrents.size()));
    }

    @Test
    public void givenSpaceReleaseDateWhenApplyThenDontUseFilter() {
        filter = new ReleaseDateFilter(" ");
        List<TorrentDetail> torrents = Stream.of(new TorrentDetail()).collect(Collectors.toList());

        List<TorrentDetail> actual = filter.apply(torrents);

        assertThat(actual, hasSize(torrents.size()));
    }

    @Test
    public void givenNotAllowFormatReleaseDateWhenApplyThenDontUseFilter() {
        filter = new ReleaseDateFilter("1234asdr");
        List<TorrentDetail> torrents = Stream.of(new TorrentDetail()).collect(Collectors.toList());

        List<TorrentDetail> actual = filter.apply(torrents);

        assertThat(actual, hasSize(torrents.size()));
    }

    @Test
    public void givenReleaseDateBeforeWhenApplyThenDontFilterOut() {
        long torrentReleaseDate = Date.valueOf(LocalDate.now()).getTime() / 1000;
        filter = new ReleaseDateFilter(yyyy_MM_dd.format(Date.valueOf(LocalDate.now().plusDays(-1))));
        TorrentDetail torrent = mock(TorrentDetail.class);
        when(torrent.getDateReleased()).thenReturn(torrentReleaseDate);
        List<TorrentDetail> torrents = Stream.of(torrent).collect(Collectors.toList());

        List<TorrentDetail> actual = filter.apply(torrents);

        assertThat(actual, hasSize(torrents.size()));
    }

    @Test
    public void givenReleaseDateAfterWhenApplyThenFilterOut() {
        long torrentReleaseDate = Date.valueOf(LocalDate.now().plusDays(-1)).getTime() / 1000;
        filter = new ReleaseDateFilter(yyyy_MM_dd.format(Date.valueOf(LocalDate.now())));
        TorrentDetail torrent = mock(TorrentDetail.class);
        when(torrent.getDateReleased()).thenReturn(torrentReleaseDate);
        List<TorrentDetail> torrents = Stream.of(torrent).collect(Collectors.toList());

        List<TorrentDetail> actual = filter.apply(torrents);

        assertThat(actual, hasSize(0));
    }

    @Test
    public void givenyyyyMMddReleaseDateBeforeWhenApplyThenDontFilterOut() {
        long torrentReleaseDate = Date.valueOf(LocalDate.now()).getTime() / 1000;
        filter = new ReleaseDateFilter(yyyyMMdd.format(Date.valueOf(LocalDate.now().plusDays(-1))));
        TorrentDetail torrent = mock(TorrentDetail.class);
        when(torrent.getDateReleased()).thenReturn(torrentReleaseDate);
        List<TorrentDetail> torrents = Stream.of(torrent).collect(Collectors.toList());

        List<TorrentDetail> actual = filter.apply(torrents);

        assertThat(actual, hasSize(torrents.size()));
    }

    @Test
    public void givenyyyyMMddReleaseDateAfterWhenApplyThenFilterOut() {
        long torrentReleaseDate = Date.valueOf(LocalDate.now().plusDays(-1)).getTime() / 1000;
        filter = new ReleaseDateFilter(yyyyMMdd.format(Date.valueOf(LocalDate.now())));
        TorrentDetail torrent = mock(TorrentDetail.class);
        when(torrent.getDateReleased()).thenReturn(torrentReleaseDate);
        List<TorrentDetail> torrents = Stream.of(torrent).collect(Collectors.toList());

        List<TorrentDetail> actual = filter.apply(torrents);

        assertThat(actual, hasSize(0));
    }

}
