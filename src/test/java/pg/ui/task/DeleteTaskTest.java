package pg.ui.task;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/* Created by Gawa 2017-11-12 */
public class DeleteTaskTest {

    private DeleteTask deleteTask;

    @Before
    public void setUp() throws Exception {
        deleteTask = new DeleteTask(null, null, null, null, null);
    }

    @Test
    public void compareTitles() throws Exception {
        String dsTitle = "The.Warfighters.S01E12.HDTV.x264-W4F_eztv_.mkv.torrent";
        String reducedTitle = "https://zoink.ch/torrent/The.Warfighters.S01E12.HDTV.x264-W4F[eztv].mkv.torrent";

        String actual = deleteTask.convertTorrentUrlToTitle(reducedTitle);

        assertThat(actual, is( equalTo(dsTitle)));
    }
}