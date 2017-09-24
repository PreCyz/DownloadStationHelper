package pg.filter;

import org.junit.After;
import org.junit.Test;
import pg.TorrentDetailBuilder;
import pg.web.model.torrent.TorrentDetail;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

/**Created by Gawa 2017-09-24*/
public class FileSizeFilterTest {
    private Filter filter;

    @After
    public void tearDown() throws Exception {
        filter = null;
    }

    @Test
    public void givenFileSize3KAndTorrentWithFileSizeLt3KWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "3K";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(2999).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenFileSize3KAndTorrentWithFileSizeEq3KWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "3K";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3000).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenFileSize3KAndTorrentWithFileSizeGt3KWhenApplyThenReturnEmptyList() {
        String fileSize = "3K";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3001).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(0));
    }

    @Test
    public void givenFileSize3MAndTorrentWithFileSize2Comma9MWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "3M";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(2900000).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenFileSize3MAndTorrentWithFileSize3MWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "3M";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3000000).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenFileSize3MAndTorrentWithFileSizeGT3MWhenApplyThenReturnEmptyList() {
        String fileSize = "3M";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3000001).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(0));
    }

    @Test
    public void givenFileSize3GAndTorrentWithFileSizeLt3GWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "3G";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(2999999999L).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenFileSize3GAndTorrentWithFileSizeEqual3GWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "3G";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3000000000L).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenFileSize3GAndTorrentWithFileSizeGt3GWhenApplyThenReturnEmptyList() {
        String fileSize = "3G";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(30000000001L).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(0));
    }

    @Test
    public void givenSomeFileSizeAndTorrentWithOtherFileSizeWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "3.65701G";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3657010000L).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenSomeFileSizeAndTorrentWithOtherFileSizeWhenApplyThenReturnListWithThatTorrent2() {
        String fileSize = "3.65701K";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3657).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void given0FileSizeAndTorrentWithOtherFileSizeWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "0";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3657).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenEmptyFileSizeAndTorrentWithOtherFileSizeWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3657).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenNullFileSizeAndTorrentWithOtherFileSizeWhenApplyThenReturnListWithThatTorrent() {
        String fileSize = "";
        filter = new FileSizeFilter(fileSize);
        TorrentDetail torrentDetail = new TorrentDetailBuilder().withSize(3657).mockTorrentDetail();

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

}