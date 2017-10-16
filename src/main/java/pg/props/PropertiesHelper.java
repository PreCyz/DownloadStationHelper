package pg.props;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.Main;
import pg.util.AppConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

/**Created by Gawa on 9/18/2017.*/
final class PropertiesHelper {

    private static final Logger logger = LogManager.getLogger(PropertiesHelper.class);

    private PropertiesHelper() {}

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

    static void storeApplicationProperties(Properties properties) throws IOException {
        storeProperties(properties, AppConstants.APPLICATION_PROPERTIES);
    }

    static void storeShowProperties(Properties properties) throws IOException {
        storeProperties(properties, AppConstants.SHOWS_PROPERTIES);
    }

    private static void storeProperties(Properties properties, String fileName) throws IOException {
        final String settingsDirPath = String.format(".%s%s", File.separator, AppConstants.SETTINGS);
        if (!Files.exists(Paths.get(settingsDirPath))) {
            Files.createDirectory(Paths.get(settingsDirPath));
        }
        final String filePath = String.format("%s%s%s", settingsDirPath, File.separator, fileName);
        Writer writer = new FileWriter(new File(filePath));
        String comments = "User configuration changed.";
        properties.store(writer, comments);
    }
}
