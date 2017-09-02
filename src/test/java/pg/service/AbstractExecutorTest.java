package pg.service;

import org.junit.Test;
import pg.Main;

import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**Created by Gawa on 15/08/17.*/
public class AbstractExecutorTest {

    private SidExecutor SidExecutor;

    @Test
    public void givenApplicationPropertiesWhenCreateFilePathThenReturnFilePath() {
        Properties properties = Main.loadProperties("application.properties");
        SidExecutor = new SidExecutor(new Properties(), properties);

        Path actual = SidExecutor.createFilePath();

        assertThat(actual, notNullValue());
        Path path = actual.toAbsolutePath();
        assertThat(path.startsWith("/home/gawa/Workspace/"), is(equalTo(true)));
        assertThat(path.toFile().getName().endsWith(".json"), is(equalTo(true)));
    }

    @Test
    public void givenShowsPropertiesWhenBuildPrecisionWordMapThenReturnMap() {
        Properties properties = Main.loadProperties("shows.properties");
        SidExecutor = new SidExecutor(properties, new Properties());

        Map<String, Integer> map = SidExecutor.buildPrecisionWordMap();

        assertThat(map.entrySet(), hasSize(2));
        assertThat(map.get("Game of Thrones,HDTV,720p"), is( equalTo(3)));
        assertThat(map.get("The Strain,HDTV,720p"), is( equalTo(3)));
    }

    @Test
    public void givenApplicationPropertiesWhenPrepareUrlThenReturnURL() {
        Properties properties = Main.loadProperties("application.properties");
        SidExecutor = new SidExecutor(new Properties(), properties);

        String actual = SidExecutor.prepareTorrentUrl();

        assertThat(actual, is( equalTo("https://eztv.ag/api/get-torrents?limit=30&page=2")));
    }

    @Test
    public void givenNoApplicationPropertiesWhenPrepareUrlThenReturnDefaultURL() {
        Properties properties = Main.loadProperties("emptyApp.properties");
        SidExecutor = new SidExecutor(new Properties(), properties);

        String actual = SidExecutor.prepareTorrentUrl();

        assertThat(actual, is( equalTo("https://eztv.ag/api/get-torrents?limit=100&page=1")));
    }

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnEmptyString() {
        Properties properties = Main.loadProperties("emptyApp.properties");
        SidExecutor = new SidExecutor(new Properties(), properties);

        String actual = SidExecutor.prepareServerUrl();

        assertThat(actual.isEmpty(), is( equalTo(true)));
    }

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {
        Properties properties = Main.loadProperties("application.properties");
        SidExecutor = new SidExecutor(new Properties(), properties);

        String actual = SidExecutor.prepareServerUrl();

        assertThat(actual, is( equalTo("http://some.address.com:5000")));
    }

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpsServerAddress() {
        Properties properties = Main.loadProperties("applicationNoPort.properties");
        SidExecutor = new SidExecutor(new Properties(), properties);

        String actual = SidExecutor.prepareServerUrl();

        assertThat(actual, is( equalTo("https://some.address.com:5001")));
    }
}