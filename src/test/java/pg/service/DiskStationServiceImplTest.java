package pg.service;

import org.junit.Test;
import pg.util.PropertyLoader;

import java.util.Collections;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**Created by Gawa 2017-09-15*/
public class DiskStationServiceImplTest {

    private DiskStationServiceImpl diskStationService;

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {
        Properties properties = PropertyLoader.loadApplicationProperties();
        diskStationService = new DiskStationServiceImpl(properties, Collections.emptyList());

        String actual = diskStationService.prepareServerUrl();

        assertThat(actual, is( equalTo("http://some.address.com:5000")));
    }

}