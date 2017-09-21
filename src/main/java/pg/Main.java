package pg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.executor.Launcher;
import pg.loader.ApplicationPropertiesLoader;
import pg.service.MatchByImdbService;
import pg.service.MatchServiceImpl;

import java.util.Arrays;

/**Created by Gawa on 15/08/17.*/
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start of application.");
        try {
            ApplicationPropertiesLoader instance = ApplicationPropertiesLoader.getInstance();
            instance.extractUsernameFromArgs(args);
            instance.extractPasswordFromArgs(args);

            Runnable launcher = new Launcher(new MatchServiceImpl());
            if (isFilterByImdb(args)) {
                logger.info("Running in imdb filtering mode.");
                launcher = new Launcher(new MatchByImdbService());
            }
            launcher.run();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }

        logger.info("End of application.");
        System.exit(0);
    }

    private static boolean isFilterByImdb(String[] args) {
        return Arrays.stream(args).anyMatch(arg -> arg.equals("imdbMode"));
    }
}
