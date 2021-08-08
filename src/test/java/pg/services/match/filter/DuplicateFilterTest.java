package pg.services.match.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pg.web.torrent.TorrentDetail;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/** Created by Gawa 2017-10-30 */
@ExtendWith(MockitoExtension.class)
public class DuplicateFilterTest {

    private DuplicateFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new DuplicateFilter();
    }

    @Test
    public void givenOneDifferentWordInTitleWhenApplyThenFilterOutTheSmallerOne() {
        TorrentDetail torrentSmaller = mock(TorrentDetail.class);
        when(torrentSmaller.getTitle()).thenReturn("The Walking Dead S08E02 720p HDTV x264-AVS EZTV");
        when(torrentSmaller.getSize()).thenReturn("1");
        when(torrentSmaller.getSeason()).thenReturn("8");
        when(torrentSmaller.getEpisode()).thenReturn("2");
        TorrentDetail torrentBigger = mock(TorrentDetail.class);
        when(torrentBigger.getTitle()).thenReturn("The Walking Dead S08E02 720p HDTV x264-FLEET EZTV");
        when(torrentBigger.getSize()).thenReturn("2");
        when(torrentBigger.getSeason()).thenReturn("8");
        when(torrentBigger.getEpisode()).thenReturn("2");

        List<TorrentDetail> actual = filter.apply(Arrays.asList(torrentSmaller, torrentBigger));

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getTitle()).isEqualTo(torrentBigger.getTitle());
    }

    @Test
    public void given3DifferentTitlesWhenApplyThenReturn3() {
        TorrentDetail torrentSmaller = mock(TorrentDetail.class);
        lenient().when(torrentSmaller.getTitle()).thenReturn("The Walking Dead S08E02 720p HDTV x264-AVS EZTV");
        lenient().when(torrentSmaller.getSize()).thenReturn("1");
        TorrentDetail torrentBigger = mock(TorrentDetail.class);
        lenient().when(torrentBigger.getTitle()).thenReturn("Walking Dead S08E02 720p HDTV x264-FLEET EZTV");
        lenient().when(torrentBigger.getSize()).thenReturn("2");
        TorrentDetail torrent = mock(TorrentDetail.class);
        lenient().when(torrent.getTitle()).thenReturn("The Walking S08E02 720p HDTV x264-FLEET EZTV");
        lenient().when(torrent.getSize()).thenReturn("2");

        List<TorrentDetail> actual = filter.apply(Arrays.asList(torrentSmaller, torrentBigger, torrent));

        assertThat(actual).hasSize(3);
    }

    @Test
    public void given6TitlesWhenApplyThenReturn3() {
        TorrentDetail ts1 = mock(TorrentDetail.class);
        when(ts1.getTitle()).thenReturn("The Walking Dead S08E02 720p HDTV x264-AVS EZTV");
        when(ts1.getSize()).thenReturn("1");
        when(ts1.getSeason()).thenReturn("8");
        when(ts1.getEpisode()).thenReturn("2");
        TorrentDetail ts2 = mock(TorrentDetail.class);
        when(ts2.getTitle()).thenReturn("The Walking Dead S08E02 720p HDTV x264-FLEET EZTV");
        when(ts2.getSize()).thenReturn("2");
        when(ts2.getSeason()).thenReturn("8");
        when(ts2.getEpisode()).thenReturn("2");
        TorrentDetail t1 = mock(TorrentDetail.class);
        when(t1.getTitle()).thenReturn("Walking Dead S08E02 720p HDTV x264-AVS EZTV");
        when(t1.getSize()).thenReturn("2");
        when(t1.getSeason()).thenReturn("8");
        when(t1.getEpisode()).thenReturn("2");
        TorrentDetail t2 = mock(TorrentDetail.class);
        when(t2.getTitle()).thenReturn("Walking Dead S08E02 720p HDTV x264-FLEET EZTV");
        when(t2.getSize()).thenReturn("1");
        when(t2.getSeason()).thenReturn("8");
        when(t2.getEpisode()).thenReturn("2");
        TorrentDetail t3 = mock(TorrentDetail.class);
        when(t3.getTitle()).thenReturn("The Walking S08E02 720p HDTV x264-FLEET EZTV");
        when(t3.getSize()).thenReturn("1");
        when(t3.getSeason()).thenReturn("8");
        when(t3.getEpisode()).thenReturn("2");
        TorrentDetail t4 = mock(TorrentDetail.class);
        when(t4.getTitle()).thenReturn("The Walking S08E02 720p HDTV x264-AVS EZTV");
        when(t4.getSize()).thenReturn("2");
        when(t4.getSeason()).thenReturn("8");
        when(t4.getEpisode()).thenReturn("2");

        List<TorrentDetail> actual = filter.apply(Arrays.asList(ts1, ts2, t1, t2, t3, t4));

        assertThat(actual).hasSize(3);
        assertThat(actual.stream().filter(t->t.getSize().equals("2")).count()). isEqualTo(3L);
    }

    @Test
    public void given6TitlesAllDifferentEpisodesWhenApplyThenReturn6() {
        TorrentDetail ts1 = mock(TorrentDetail.class);
        lenient().when(ts1.getTitle()).thenReturn("The Walking Dead S08E01 720p HDTV x264-FLEET EZTV");
        lenient().when(ts1.getSize()).thenReturn("1");
        lenient().when(ts1.getSeason()).thenReturn("8");
        lenient().when(ts1.getEpisode()).thenReturn("1");
        TorrentDetail ts2 = mock(TorrentDetail.class);
        lenient().when(ts2.getTitle()).thenReturn("The Walking Dead S08E02 720p HDTV x264-FLEET EZTV");
        lenient().when(ts2.getSize()).thenReturn("2");
        lenient().when(ts2.getSeason()).thenReturn("8");
        lenient().when(ts2.getEpisode()).thenReturn("2");
        TorrentDetail t1 = mock(TorrentDetail.class);
        lenient().when(t1.getTitle()).thenReturn("Walking Dead S08E03 720p HDTV x264-FLEET EZTV");
        lenient().when(t1.getSize()).thenReturn("2");
        lenient().when(t1.getSeason()).thenReturn("8");
        lenient().when(t1.getEpisode()).thenReturn("3");
        TorrentDetail t2 = mock(TorrentDetail.class);
        lenient().when(t2.getTitle()).thenReturn("Walking Dead S08E04 720p HDTV x264-FLEET EZTV");
        lenient().when(t2.getSize()).thenReturn("1");
        lenient().when(t2.getSeason()).thenReturn("8");
        lenient().when(t2.getEpisode()).thenReturn("4");
        TorrentDetail t3 = mock(TorrentDetail.class);
        lenient().when(t3.getTitle()).thenReturn("The Walking S08E05 720p HDTV x264-FLEET EZTV");
        lenient().when(t3.getSize()).thenReturn("1");
        lenient().when(t3.getSeason()).thenReturn("8");
        lenient().when(t3.getEpisode()).thenReturn("5");
        TorrentDetail t4 = mock(TorrentDetail.class);
        lenient().when(t4.getTitle()).thenReturn("The Walking S08E06 720p HDTV x264-FLEET EZTV");
        lenient().when(t4.getSize()).thenReturn("2");
        lenient().when(t4.getSeason()).thenReturn("8");
        lenient().when(t4.getEpisode()).thenReturn("6");

        List<TorrentDetail> actual = filter.apply(Arrays.asList(ts1, ts2, t1, t2, t3, t4));

        assertThat(actual).hasSize(6);
    }

    @Test
    public void givenOneDifferentWordInTitleWhenIsTitlesDuplicateThenReturnTrue() {
        String firstTitle = "The Walking Dead S08E02 720p HDTV x264-AVS EZTV";
        String secondTitle ="The Walking Dead S08E02 720p HDTV x264-FLEET EZTV";

        boolean actual = filter.isTitlesDuplicate(firstTitle, secondTitle);

        assertThat(actual).isTrue();
    }

    @Test
    public void givenOneDifferentWordInTitlesWhenIsTitlesDuplicateThenReturnTrue() {
        String firstTitle = "The Walking Dead S08E02 720p HDTV x264-AVS";
        String secondTitle ="The Walking Dead S08E02 720p HDTV x264-FLEET EZTV";

        boolean actual = filter.isTitlesDuplicate(firstTitle, secondTitle);

        assertThat(actual).isTrue();
    }

    @Test
    public void givenEqualsTitlesWhenIsTitlesDuplicateThenReturnTrue() {
        String firstTitle = "The Walking Dead S08E02 720p HDTV x264-FLEET EZTV";
        String secondTitle ="The Walking Dead S08E02 720p HDTV x264-FLEET EZTV";

        boolean actual = filter.isTitlesDuplicate(firstTitle, secondTitle);

        assertThat(actual).isTrue();
    }

    @Test
    public void givenDifferentTitlesWhenIsTitlesDuplicateThenReturnTrue() {
        String firstTitle = "Walking Dead S08E02 720p HDTV x264-FLEET EZTV";
        String secondTitle ="The Walking Dead S08E02 720p HDTV x264-FLEET EZTV";

        boolean actual = filter.isTitlesDuplicate(firstTitle, secondTitle);

        assertThat(actual).isTrue();
    }

    @Test
    public void givenTwoTitlesWithOnlyOneDifferenceWhenIsTitlesDuplicateThenReturnFalse() {
        String firstTitle = "The Walking Dead S08E03 720p HDTV x264-FLEET EZTV";
        String secondTitle ="The Walking Dead S08E02 720p HDTV x264-FLEET EZTV";

        boolean actual = filter.isTitlesDuplicate(firstTitle, secondTitle);

        assertThat(actual).isFalse();
    }

    @Test
    public void givenTwoTitlesWithTwoDifferencesWhenIsTitlesDuplicateThenReturnFalse() {
        String firstTitle = "The Walking Dead S08E03 720p HDTV x264-AVS";
        String secondTitle ="The Walking Dead S08E02 720p HDTV x264-FLEET EZTV";

        boolean actual = filter.isTitlesDuplicate(firstTitle, secondTitle);

        assertThat(actual).isFalse();
    }

    @Test
    public void givenTitlesWithTheSameSeasonAndEpisode_when_isTitlesDuplicate_thenReturnTrue() {
        String firstTitle = "The 100 S05E01 HDTV x264-SVA EZTV";
        String secondTitle ="The 100 S05E01 720p HDTV x264-SVA EZTV";

        boolean actual = filter.isTitlesDuplicate(firstTitle, secondTitle);

        assertThat(actual).isTrue();
    }

    @Test
    public void givenDifferentTitlesWithTheSameSeasonAndEpisode_when_isTitlesDuplicate_thenReturnFalse() {
        String firstTitle = "Raven S05E01 720p HDTV x264-SVA EZTV";
        String secondTitle ="The 100 S05E01 720p HDTV x264-SVA EZTV";

        boolean actual = filter.isTitlesDuplicate(firstTitle, secondTitle);

        assertThat(actual).isFalse();
    }

    @Test
    public void givenTitle_when_getSeasonEpisodeIndex_then_returnProperIdx() {
        String title = "Marvels Agents of S H I E L D S05E18 720p HDTV x264-KILLERS EZTV";
        int idx = filter.getSeasonEpisodeIndex(title.split(" "));
        assertThat(idx).isEqualTo(9);
    }
}