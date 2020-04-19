package pg.services;

import org.junit.Test;

/**Created by Gawa 2017-09-15*/
public class FileServiceImplTest {
    @Test
    public void writeTorrentsToFile() throws Exception {
    }

    @Test
    public void buildImdbMap1() throws Exception {
    }

    @Test
    public void writeImdbMapToFile() throws Exception {
    }

    @Test
    public void createFilePath() throws Exception {
    }

    private FileServiceImpl fileService;

    @Test
    public void givenApplicationPropertiesWhenCreateFilePathThenReturnFilePath() {
        fileService = new FileServiceImpl();

    /*    Path actual = fileService.createFilePath("dada");

        assertThat(actual, notNullValue());
        Path path = actual.toAbsolutePath();
        assertThat(path.startsWith("/home/gawa/Workspace/"), is(equalTo(true)));
        assertThat(path.toFile().getName().endsWith("dada.json"), is(equalTo(true)));*/
    }

    @Test
    public void buildImdbMap() throws Exception {

    }
}