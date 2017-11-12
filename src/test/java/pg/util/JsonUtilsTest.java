package pg.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.DeleteResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
        resource = JsonUtilsTest.class.getClassLoader().getResource("./matchTorrents.json");
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
        Map<String, ReducedDetail> map = JsonUtils.convertMatchTorrentsFromFile(Paths.get(resource.getPath()));
        assertThat(map.isEmpty(), is( equalTo(false)));
        assertThat(map, aMapWithSize(2));
        map.keySet().forEach(key -> assertThat(map.get(key), instanceOf(ReducedDetail.class)));
    }

    @Test
    public void convertDeleteResponseFromString() throws Exception {
        String deleteResponse = "[{ \"error\":405, \"id\":\"dbid_001\" }," +
                "{ \"error\":0, \"id\":\"dbid_002\" }]";
        List<DeleteResponse> actual = JsonUtils.convertDeleteResponseFromString(deleteResponse);

        assertThat(actual, hasSize(2));
        assertThat(actual.get(0).getId(), is( equalTo("dbid_001")));
        assertThat(actual.get(0).getError(), is( equalTo(405)));
        assertThat(actual.get(1).getId(), is( equalTo("dbid_002")));
        assertThat(actual.get(1).getError(), is( equalTo(0)));
    }
}