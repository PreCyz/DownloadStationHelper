package pg.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pg.web.torrent.ReducedDetail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**Created by Gawa 2017-09-16*/
public class JsonUtilsTest {

    private static URL resource;

    @BeforeAll
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
            assertThat(map).hasSize(2);
            map.keySet().forEach(key -> assertThat(map.get(key)).isInstanceOf(ReducedDetail.class));
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    public void convertFromFile() throws Exception {
        Path jsonPath = Paths.get(resource.toURI());
        Map<String, ReducedDetail> map = JsonUtils.convertMatchTorrentsFromFile(jsonPath);
        assertThat(map).isNotEmpty();
        assertThat(map).hasSize(2);
        map.keySet().forEach(key -> assertThat(map.get(key)).isInstanceOf(ReducedDetail.class));
    }

}