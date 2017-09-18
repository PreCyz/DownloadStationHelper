package pg.service;

import org.junit.Test;
import pg.loader.ApplicationPropertiesLoader;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**Created by Gawa 2017-09-15*/
public class DiskStationServiceImplTest {

    private DiskStationServiceImpl diskStationService;

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {
        //diskStationService = new DiskStationServiceImpl();

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