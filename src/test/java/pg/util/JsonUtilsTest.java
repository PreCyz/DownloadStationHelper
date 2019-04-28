package pg.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import pg.web.torrent.ReducedDetail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.fail;

/**Created by Gawa 2017-09-16*/
public class JsonUtilsTest {

    private static URL resource;

    @BeforeClass
    public static void beforeClass() {
        resource = JsonUtilsTest.class.getClassLoader().getResource("matchTorrents.json");
    }

    @Test
    public void convertFromString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, ReducedDetail> map = mapper.readValue(
                    new File(resource.getPath()),
                    new TypeReference<Map<String, ReducedDetail>>(){});
            assertThat(map, aMapWithSize(2));
            map.keySet().forEach(key -> assertThat(map.get(key), instanceOf(ReducedDetail.class)));
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    public void convertFromFile() throws Exception {
        Path jsonPath = Paths.get(resource.toURI());
        Map<String, ReducedDetail> map = JsonUtils.convertMatchTorrentsFromFile(jsonPath);
        assertThat(map.isEmpty(), is( equalTo(false)));
        assertThat(map, aMapWithSize(2));
        map.keySet().forEach(key -> assertThat(map.get(key), instanceOf(ReducedDetail.class)));
    }

}