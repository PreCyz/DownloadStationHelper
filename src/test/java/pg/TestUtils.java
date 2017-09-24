package pg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**Created by Gawa on 12/09/17.*/
public final class TestUtils {

    private TestUtils() {}

    public static Properties loadTestProperties(String fileName) {
        try (InputStream resourceIS = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties properties = new Properties();
            properties.load(resourceIS);
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("No %s file.", fileName));
        }
    }

}
