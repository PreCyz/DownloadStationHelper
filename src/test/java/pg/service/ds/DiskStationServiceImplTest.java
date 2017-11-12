package pg.service.ds;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**Created by Gawa 2017-09-15*/
@RunWith(MockitoJUnitRunner.class)
public class DiskStationServiceImplTest {

    private DiskStationServiceImpl diskStationService;

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {

        diskStationService = new DiskStationServiceImpl(Collections.emptyList());

        String actual = diskStationService.prepareServerUrl();

        assertThat(actual, is( equalTo("http://some.address.com:5000")));
    }

    @Test
    public void givenListInsideListWhenCalculatingSizeOfTheCollectionWithTheStreamsThenReturnProperSize() {
        TorrentResponse tr = mock(TorrentResponse.class);
        when(tr.getTorrents()).thenReturn(
                Arrays.asList(new TorrentDetail(), new TorrentDetail(), new TorrentDetail())
        );
        List<TorrentResponse> responses = Collections.singletonList(tr);

        assertThat(3, is( equalTo(responses.stream().mapToInt(r->r.getTorrents().size()).sum())));
    }
}