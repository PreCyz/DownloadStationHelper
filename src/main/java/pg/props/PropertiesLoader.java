package pg.props;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.Main;
import pg.util.AppConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**Created by Gawa on 9/18/2017.*/
final class PropertiesLoader {

    private static final Logger logger = LogManager.getLogger(PropertiesLoader.class);

    private PropertiesLoader() {}

    static Optional<Properties> loadProperties(String fileName) {
        String applicationProperties = "." + File.separator + AppConstants.SETTINGS + File.separator + fileName;
        try (FileInputStream fis = new FileInputStream(applicationProperties)) {
            Properties properties = new Properties();
            properties.load(fis);
            logger.info("User {} file loaded.", fileName);
            return Optional.of(properties);
        } catch (IOException ex) {
            logger.info("Can't find user's {}. Loading default one.", fileName);
        }

        return Optional.empty();
    }

    static Properties loadDefaultProperties(String fileName) {
        try (InputStream resourceIS = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties properties = new Properties();
            properties.load(resourceIS);
            logger.info("Program {} file loaded.", fileName);
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("No %s file.", fileName));
        }
    }
}
