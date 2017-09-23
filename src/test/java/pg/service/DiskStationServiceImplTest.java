package pg.service;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**Created by Gawa 2017-09-15*/
public class DiskStationServiceImplTest {

    private DiskStationServiceImpl diskStationService;

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {

        diskStationService = new DiskStationServiceImpl(Collections.emptyList());

        String actual = diskStationService.prepareServerUrl();

        assertThat(actual, is( equalTo("http://some.address.com:5000")));
    }

    @Test
    public void buildLoginUrl() throws Exception {

    }

    @Test
    public void buildCreateTaskUrl() throws Exception {

    }

    @Test
    public void buildTaskListUrl() throws Exception {

    }

    @Test
    public void buildLogoutUrl() throws Exception {

    }
}