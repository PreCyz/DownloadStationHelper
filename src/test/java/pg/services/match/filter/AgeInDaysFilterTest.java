package pg.services.match.filter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pg.TorrentDetailBuilder;
import pg.web.torrent.TorrentDetail;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**Created by Gawa 2017-09-24*/
public class AgeInDaysFilterTest {

    private Filter filter;

    @AfterEach
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

        assertThat(actual).hasSize(1);
    }

    @Test
    public void givenTorrentAndAgeInDaysEq0WhenApplyThenReturnThatTorrent() {
        int torrentAgeIdDays = 0;
        TorrentDetail torrentDetail = new TorrentDetailBuilder()
                .withDateReleased(System.currentTimeMillis() / 1000)
                .mockTorrentDetail();
        filter = new AgeInDaysFilter(torrentAgeIdDays);

        List<TorrentDetail> actual = filter.apply(Collections.singletonList(torrentDetail));

        assertThat(actual).hasSize(1);
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

        assertThat(actual).isEmpty();
    }

}