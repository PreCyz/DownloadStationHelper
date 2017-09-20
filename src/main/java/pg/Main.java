package pg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.executor.GetTorrentLauncher;
import pg.loader.ApplicationPropertiesLoader;

/**Created by Gawa on 15/08/17.*/
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start of application.");
        try {
            ApplicationPropertiesLoader instance = ApplicationPropertiesLoader.getInstance();
            instance.extractUsernameFromArgs(args);
            instance.extractPasswordFromArgs(args);

            new GetTorrentLauncher().run();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }

        logger.info("End of application.");
        System.exit(0);
    }
}
