package pg.ui.window.controller.task.atomic.call.ds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pg.program.ApiName;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;
import pg.web.torrent.ReducedDetail;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateTaskCallTest {

    private CreateTaskCall call;

    @BeforeEach
    public void setUp() {
        call = new CreateTaskCall("sid", Collections.emptyList(), mockDsApiDetails());
    }

    private DSApiDetails mockDsApiDetails() {
        DSApiDetails downloadStationTask = mock(DSApiDetails.class);
        when(downloadStationTask.getMaxVersion()).thenReturn(1);
        when(downloadStationTask.getPath()).thenReturn("somePath");
        return downloadStationTask;
    }

    @Test
    public void givenRequiredValues_when_buildTaskUrl_then_returnProperUrl() {
        String url = call.buildTaskUrl();

        assertThat(url).isNotEmpty();
        assertThat(url).endsWith("/webapi/somePath?api=" + ApiName.DOWNLOAD_STATION_TASK +
                "&version=1" +
                "&method=" + DSTaskMethod.CREATE.method() +
                "&_sid=sid" +
                "&destination=downloads" +
                "&uri=");
    }

    @Test
    public void when_getTaskMethod_then_returnCreate() {
        assertThat(call.getTaskMethod()).isEqualTo(DSTaskMethod.CREATE);
    }

    @Test
    public void givenReducedDetailAndTypeTorrent_when_getEncodedTorrentUri_thenReturnEncodedUrl() {
        ReducedDetail reducedDetail = new ReducedDetail(
                "title",
                1,
                new Date(),
                "magnetUrl",
                "https://eztv.ag/ep/1184810/rake-s05e04-hdtv-x264-w4f/",
                "imbdId",
                "season1",
                "episode1"
        );
        String encodedUri = call.getEncodedTorrentUri(reducedDetail);

        assertThat(encodedUri).isEqualTo("https%3A%2F%2Feztv.ag%2Fep%2F1184810%2Frake-s05e04-hdtv-x264-w4f%2F");
    }
}