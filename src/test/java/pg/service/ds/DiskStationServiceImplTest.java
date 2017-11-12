package pg.service.ds;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.TorrentResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**Created by Gawa 2017-09-15*/
@RunWith(MockitoJUnitRunner.class)
public class DiskStationServiceImplTest {

    private DiskStationServiceImpl diskStationService;

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {

        diskStationService = new DiskStationServiceImpl(Collections.emptyList());

        String actual = diskStationService.prepareServerUrl();

        MatcherAssert.assertThat(actual, Matchers.is( Matchers.equalTo("http://some.address.com:5000")));
    }

    @Test
    public void givenListInsideListWhenCalculatingSizeOfTheCollectionWithTheStreamsThenReturnProperSize() {
        TorrentResponse tr = Mockito.mock(TorrentResponse.class);
        Mockito.when(tr.getTorrents()).thenReturn(
                Arrays.asList(new TorrentDetail(), new TorrentDetail(), new TorrentDetail())
        );
        List<TorrentResponse> responses = Collections.singletonList(tr);

        MatcherAssert.assertThat(3, Matchers.is( Matchers.equalTo(responses.stream().mapToInt(r->r.getTorrents().size()).sum())));
    }
}