package pg;

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

    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final String SHOWS_PROPERTIES = "shows.properties";

    public static void main(String[] args) {
        try {
            Properties application = loadProperties(APPLICATION_PROPERTIES);
            extractUsernameFromArgs(args, application);
            extractPasswordFromArgs(args, application);
            Properties shows = loadProperties(SHOWS_PROPERTIES);

            Executor executor = new SidExecutor(shows, application);
            executor.findTorrents();
            executor.matchTorrents();
            executor.writeTorrentsToFile();
            String creationMethod = application.getProperty(SettingKeys.CREATION_METHOD.key(), "COPY_FILE");
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
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getLocalizedMessage());
        }

        System.exit(0);
    }

    public static Properties loadProperties(String fileName) {
        try (InputStream resourceIS = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties properties = new Properties();
            properties.load(resourceIS);
            System.out.printf("%s file loaded.%n", fileName);
            return properties;
        } catch (IOException e) {
            System.out.printf("No %s file.%n", fileName);
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
            username.ifPresent(pass -> applications.setProperty(SettingKeys.USERNAME.key(),
                    pass.substring(pass.lastIndexOf("=") + 1)));
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
            passwd.ifPresent(pass -> applications.setProperty(SettingKeys.PASSWORD.key(),
                    pass.substring(pass.lastIndexOf("=") + 1)));
        } else {
            throw new IllegalArgumentException("No password where given. Add password to application.properties " +
                    "or run program with passwd param (passwd=somePass).");
        }
    }
}
