package pg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.executor.Launcher;
import pg.util.PropertyLoader;
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
            Properties application = PropertyLoader.getApplicationProperties();
            extractUsernameFromArgs(args, application);
            extractPasswordFromArgs(args, application);

            new Launcher().run();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }

        logger.info("End of application.");
        System.exit(0);
    }

    static void extractUsernameFromArgs(String[] args, Properties applications) {
        if (applications.containsKey(SettingKeys.USERNAME.key())) {
            return;
        }
        if (args == null || args.length == 0) {
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
