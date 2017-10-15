package pg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.executor.Launcher;
import pg.props.ApplicationPropertiesHelper;
import pg.web.model.ProgramMode;
import pg.web.model.StartParameters;

import java.util.Arrays;

/**Created by Gawa on 15/08/17.*/
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start of application.");
        try {
            ApplicationPropertiesHelper instance = ApplicationPropertiesHelper.getInstance();
            instance.extractUsername(args);
            instance.extractPassword(args);
            ProgramMode programMode = extractMode(args);

            Runnable launcher = new Launcher(programMode);
            launcher.run();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }

        logger.info("End of application.");
        System.exit(0);
    }

    private static ProgramMode extractMode(String[] args) {
        if (Arrays.stream(args).anyMatch(StartParameters.IMDB_MODE.param()::equals)) {
            logger.info("Program mode: [{}].", ProgramMode.IMDB);
            return ProgramMode.IMDB;
        }
        logger.info("Program mode: [{}].", ProgramMode.ALL);
        return ProgramMode.ALL;
    }
}
