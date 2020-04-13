package pg.service.ds;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**Created by Gawa 2017-09-15*/
@RunWith(MockitoJUnitRunner.class)
public class DiskStationServiceImplTest {

    private DiskStationServiceImpl diskStationService;

    @Test
    @Ignore
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {
        diskStationService = new DiskStationServiceImpl(Collections.emptyList());

        String actual = diskStationService.prepareServerUrl();

        assertThat(actual).isEqualTo("http://192.168.0.103:5000");
    }

    @Test
    public void givenListInsideListWhenCalculatingSizeOfTheCollectionWithTheStreamsThenReturnProperSize() {
        TorrentResponse tr = mock(TorrentResponse.class);
        when(tr.getTorrents()).thenReturn(
                Arrays.asList(new TorrentDetail(), new TorrentDetail(), new TorrentDetail())
        );
        List<TorrentResponse> responses = Collections.singletonList(tr);

        assertThat(responses.stream().mapToInt(r->r.getTorrents().size()).sum()).isEqualTo(3);
    }
}