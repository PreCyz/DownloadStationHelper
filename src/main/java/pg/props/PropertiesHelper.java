package pg.props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.FXMain;
import pg.util.AppConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

/**Created by Gawa on 9/18/2017.*/
final class PropertiesHelper {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);

    private PropertiesHelper() {}

    static Optional<Properties> loadProperties(String fileName) {
        String applicationProperties = "." + File.separator + AppConstants.SETTINGS + File.separator + fileName;
        try (InputStream is = new FileInputStream(applicationProperties)) {
            Properties properties = new Properties();
            properties.load(is);
            logger.info("User {} file loaded.", fileName);
            return Optional.of(properties);
        } catch (IOException ex) {
            logger.info("Can't find user's {}. Loading default one.", fileName);
        }

        return Optional.empty();
    }

    static Properties loadDefaultProperties(String fileName) {
        try (InputStream resourceIS = FXMain.class.getClassLoader().getResourceAsStream(fileName)) {
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

    private static void storeProperties(Properties properties, String fileName) throws IOException {
        final String settingsDirPath = String.format(".%s%s", File.separator, AppConstants.SETTINGS);
        if (!Files.exists(Paths.get(settingsDirPath))) {
            Files.createDirectory(Paths.get(settingsDirPath));
        }
        Writer writer = new FileWriter(Paths.get(settingsDirPath, fileName).toFile());
        String comments = "User configuration changed.";
        synchronized (PropertiesHelper.class) {
            properties.store(writer, comments);
        }
    }
}
