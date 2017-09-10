package pg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.service.Executor;
import pg.service.SidExecutor;
import pg.web.model.DSMethod;
import pg.web.model.SettingKeys;
import pg.web.model.StartParameters;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

/**Created by Gawa on 15/08/17.*/
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final String SHOWS_PROPERTIES = "shows.properties";

    public static void main(String[] args) {
        logger.info("Start of application.");
        try {
            Properties application = loadProperties(APPLICATION_PROPERTIES);
            extractUsernameFromArgs(args, application);
            extractPasswordFromArgs(args, application);
            Properties shows = loadProperties(SHOWS_PROPERTIES);

            Executor executor = new SidExecutor(shows, application);
            executor.findTorrents();
            executor.matchTorrents();
            if (executor.hasFoundTorrents()) {
                if (!"".equals(application.getProperty(SettingKeys.FILE_PATH.key(), ""))) {
                    executor.writeTorrentsToFile();
                }
                String creationMethod = application.getProperty(SettingKeys.CREATION_METHOD.key(),
                        DSMethod.COPY_FILE.name());
                switch (DSMethod.valueOf(creationMethod)) {
                    case COPY_FILE:
                        executor.writeTorrentsOnDS();
                        break;
                    case REST:
                        executor.prepareAvailableOperations();
                        executor.loginToDiskStation();
                        executor.createDownloadStationTasks();
                        executor.listOfTasks();
                        executor.logoutFromDiskStation();
                        break;
                }
            } else {
                logger.info("No matching torrents found.");
            }
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getLocalizedMessage());
        }

        logger.info("End of application.");
        System.exit(0);
    }

    public static Properties loadProperties(String fileName) {
        try (InputStream resourceIS = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties properties = new Properties();
            properties.load(resourceIS);
            logger.info("{} file loaded", fileName);
            return properties;
        } catch (IOException e) {
            logger.error("No {} file.", fileName);
            System.exit(0);
        }
        throw new NullPointerException();
    }

    static void extractUsernameFromArgs(String[] args, Properties applications) {
        if (applications.containsKey(SettingKeys.USERNAME.key())) {
            return;
        }
        if (args == null || args.length ==0) {
            throw new IllegalArgumentException("No userName where given. Add username to application.properties " +
                    "or run program with username param (username=login).");
        }
        Optional<String> username = Arrays.stream(args)
                .filter(arg -> arg.contains(StartParameters.USERNAME.param()))
                .findFirst();
        if (username.isPresent()) {
            String usernameArg = username.get();
            if (usernameArg.contains("'")) {
                usernameArg = usernameArg.replaceAll("'", "");
            }
            applications.setProperty(SettingKeys.USERNAME.key(),
                    usernameArg.substring(usernameArg.lastIndexOf("=") + 1));
        } else {
            throw new IllegalArgumentException("No userName where given. Add username to application.properties " +
                    "or run program with username param (username=login).");
        }
    }

    static void extractPasswordFromArgs(String[] args, Properties applications) {
        if (applications.containsKey(SettingKeys.PASSWORD.key())) {
            return;
        }
        if (args == null || args.length ==0) {
            throw new IllegalArgumentException("No password where given. Add password to application.properties " +
                    "or run program with passwd param (passwd=somePass).");
        }
        Optional<String> passwd = Arrays.stream(args)
                .filter(arg -> arg.contains(StartParameters.PASSWORD.param()))
                .findFirst();
        if (passwd.isPresent()) {
            String pass = passwd.get();
            if (pass.contains("'")) {
                pass = pass.replaceAll("'", "");
            }
            applications.setProperty(SettingKeys.PASSWORD.key(), pass.substring(pass.lastIndexOf("=") + 1));
        } else {
            throw new IllegalArgumentException("No password where given. Add password to application.properties " +
                    "or run program with passwd param (passwd=somePass).");
        }
    }
}
