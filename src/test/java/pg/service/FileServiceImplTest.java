package pg.service;

import org.junit.Test;
import pg.util.PropertyLoader;

import java.nio.file.Path;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**Created by Gawa 2017-09-15*/
public class FileServiceImplTest {

    private FileServiceImpl fileService;

    @Test
    public void givenApplicationPropertiesWhenCreateFilePathThenReturnFilePath() {
        Properties properties = PropertyLoader.loadApplicationProperties();
        fileService = new FileServiceImpl(properties);

        Path actual = fileService.createFilePath("dada");

        assertThat(actual, notNullValue());
        Path path = actual.toAbsolutePath();
        assertThat(path.startsWith("/home/gawa/Workspace/"), is(equalTo(true)));
        assertThat(path.toFile().getName().endsWith("dada.json"), is(equalTo(true)));
    }

}