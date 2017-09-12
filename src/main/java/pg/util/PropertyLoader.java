package pg.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.Main;
import pg.web.model.SettingKeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**Created by Gawa on 12/09/17.*/
public final class PropertyLoader {

    private static final Logger logger = LogManager.getLogger(PropertyLoader.class);

    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final String SHOWS_PROPERTIES = "shows.properties";

    private PropertyLoader() {}

    public static Properties loadApplicationProperties() {
        Optional<Properties> applicationOpt = loadProperties(APPLICATION_PROPERTIES);
        if (applicationOpt.isPresent()) {
            Properties defaultApplication = loadDefaultProperties(APPLICATION_PROPERTIES);
            Properties application = applicationOpt.get();
            if (!application.containsKey(SettingKeys.URL.key())) {
                application.put(SettingKeys.URL.key(), defaultApplication.getProperty(SettingKeys.URL.key()));
            }
            if (!application.containsKey(SettingKeys.API_INFO.key())) {
                application.put(SettingKeys.API_INFO.key(),
                        defaultApplication.getProperty(SettingKeys.API_INFO.key()));
            }
            if (!application.containsKey(SettingKeys.LIMIT.key())) {
                application.put(SettingKeys.LIMIT.key(), defaultApplication.getProperty(SettingKeys.LIMIT.key()));
            }
            return application;
        }
        return loadDefaultProperties(APPLICATION_PROPERTIES);
    }

    protected static Optional<Properties> loadProperties(String fileName) {
        String applicationProperties = "."+ File.separator + fileName;
        try (FileInputStream fis = new FileInputStream(applicationProperties)) {
            Properties properties = new Properties();
            properties.load(fis);
            logger.info("{} file loaded", fileName);
            return Optional.of(properties);
        } catch (IOException ex) {
            logger.info("Can't find user's {}. Loading default one.", fileName);
        }

        return Optional.empty();
    }

    protected static Properties loadDefaultProperties(String fileName) {
        try (InputStream resourceIS = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties properties = new Properties();
            properties.load(resourceIS);
            logger.info("{} file loaded", fileName);
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("No %s file.", fileName));
        }
    }

    public static Properties loadShowsProperties() {
        Optional<Properties> showsOpt = loadProperties(SHOWS_PROPERTIES);
        return showsOpt.orElseGet(() -> loadDefaultProperties(SHOWS_PROPERTIES));
    }
}
