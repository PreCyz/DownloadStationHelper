package pg.services.ds;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Created by Gawa 2017-09-15 */
@ExtendWith(MockitoExtension.class)
public class DiskStationServiceImplTest {

    private DiskStationServiceImpl diskStationService;

    @AfterEach
    public void tearDown() {
        //diskStationService.logoutFromDiskStation();
    }

    @Test
    @Disabled
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {
        diskStationService = new DiskStationServiceImpl(Collections.emptyList());

        String actual = diskStationService.prepareServerUrl();

        assertThat(actual).isEqualTo("http://192.168.0.103:5000");
    }

    @Test
    @Disabled
    public void test() throws Exception {
        diskStationService = new DiskStationServiceImpl(Collections.emptyList());

        diskStationService.prepareAvailableOperations();
        diskStationService.loginToDiskStation();
        diskStationService.btSearchStart("clone wars");

        boolean isSearchFinished;
        do {
            Thread.sleep(5000);
            isSearchFinished = diskStationService.btSearchList();

        } while (!isSearchFinished);

        diskStationService.btSearchClean();
    }

    @Test
    @Disabled
    public void testCategories() {
        diskStationService = new DiskStationServiceImpl(Collections.emptyList());

        diskStationService.prepareAvailableOperations();
        diskStationService.loginToDiskStation();
        diskStationService.btSearchModules();
        diskStationService.btSearchCategories();
    }

    @Test
    public void givenListInsideListWhenCalculatingSizeOfTheCollectionWithTheStreamsThenReturnProperSize() {
        TorrentResponse tr = mock(TorrentResponse.class);
        when(tr.getTorrents()).thenReturn(
                Arrays.asList(new TorrentDetail(), new TorrentDetail(), new TorrentDetail())
        );
        List<TorrentResponse> responses = Collections.singletonList(tr);

        assertThat(responses.stream().mapToInt(r -> r.getTorrents().size()).sum()).isEqualTo(3);
    }
}