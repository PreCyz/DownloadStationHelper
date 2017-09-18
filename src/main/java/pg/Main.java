package pg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.executor.Launcher;
import pg.loader.ApplicationPropertiesLoader;
import pg.web.model.SettingKeys;
import pg.web.model.StartParameters;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

/**Created by Gawa on 15/08/17.*/
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start of application.");
        try {
            ApplicationPropertiesLoader instance = ApplicationPropertiesLoader.getInstance();
            instance.extractUsernameFromArgs(args);
            instance.extractPasswordFromArgs(args);

            new Launcher().run();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }

        logger.info("End of application.");
        System.exit(0);
    }
}
