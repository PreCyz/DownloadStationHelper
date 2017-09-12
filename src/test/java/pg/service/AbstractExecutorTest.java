package pg.service;

import org.junit.Test;
import pg.util.PropertyLoader;

import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**Created by Gawa on 15/08/17.*/
public class AbstractExecutorTest {

    private SidExecutor sidExecutor;

    @Test
    public void givenApplicationPropertiesWhenCreateFilePathThenReturnFilePath() {
        Properties properties = PropertyLoader.loadApplicationProperties();
        sidExecutor = new SidExecutor(new Properties(), properties);

        Path actual = sidExecutor.createFilePath();

        assertThat(actual, notNullValue());
        Path path = actual.toAbsolutePath();
        assertThat(path.startsWith("/home/gawa/Workspace/"), is(equalTo(true)));
        assertThat(path.toFile().getName().endsWith(".json"), is(equalTo(true)));
    }

    @Test
    public void givenShowsPropertiesWhenBuildPrecisionWordMapThenReturnMap() {
        Properties properties = PropertyLoader.loadShowsProperties();
        sidExecutor = new SidExecutor(properties, new Properties());

        Map<String, Integer> map = sidExecutor.buildPrecisionWordMap();

        assertThat(map.entrySet(), hasSize(2));
        assertThat(map.get("Game of Thrones,HDTV,720p"), is( equalTo(3)));
        assertThat(map.get("The Strain,HDTV,720p"), is( equalTo(3)));
    }

    @Test
    public void givenApplicationPropertiesWhenPrepareUrlThenReturnURL() {
        Properties properties = PropertyLoader.loadApplicationProperties();
        sidExecutor = new SidExecutor(new Properties(), properties);

        String actual = sidExecutor.prepareTorrentUrl(2);

        assertThat(actual, is( equalTo("https://eztv.ag/api/get-torrents?limit=30&page=2")));
    }

    @Test
    public void givenNoServerAddressInSettingsWhenPrepareServerUrlTheReturnHttpServerAddress() {
        Properties properties = PropertyLoader.loadApplicationProperties();
        SidExecutor sidExecutor= new SidExecutor(new Properties(), properties);

        String actual = sidExecutor.prepareServerUrl();

        assertThat(actual, is( equalTo("http://some.address.com:5000")));
    }
}