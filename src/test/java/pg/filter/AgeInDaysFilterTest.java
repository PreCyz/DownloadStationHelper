package pg.filter;

import org.junit.After;
import org.junit.Test;
import pg.TorrentDetailBuilder;
import pg.web.model.torrent.TorrentDetail;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

/**Created by Gawa 2017-09-24*/
public class AgeInDaysFilterTest {

    private Filter filter;

    @After
    public void tearDown() throws Exception {
        filter = null;
    }

    @Test
    public void givenTorrentYoungerThan1DayWhenApplyThenReturnThatTorrent() {
        int torrentAgeIdDays = 1;
        TorrentDetail torrentDetail = new TorrentDetailBuilder()
                .withDateReleased(System.currentTimeMillis() / 1000)
                .mockTorrentDetail();
        filter = new AgeInDaysFilter(torrentAgeIdDays);

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenTorrentAndAgeInDaysEq0WhenApplyThenReturnThatTorrent() {
        int torrentAgeIdDays = 0;
        TorrentDetail torrentDetail = new TorrentDetailBuilder()
                .withDateReleased(System.currentTimeMillis() / 1000)
                .mockTorrentDetail();
        filter = new AgeInDaysFilter(torrentAgeIdDays);

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void givenTorrentOlderThan1DayWhenApplyThenReturnThatTorrent() {
        int torrentAgeIdDays = 1;
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -(torrentAgeIdDays + 1));
        TorrentDetail torrentDetail = new TorrentDetailBuilder()
                .withDateReleased(yesterday.getTimeInMillis() / 1000)
                .mockTorrentDetail();
        filter = new AgeInDaysFilter(torrentAgeIdDays);

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual, hasSize(0));
    }

}