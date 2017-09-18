package pg.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.util.AppConstants;
import pg.web.model.SettingKeys;
import pg.web.model.StartParameters;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

/**Created by Gawa on 12/09/17.*/
public final class ApplicationPropertiesLoader {

    private static ApplicationPropertiesLoader instance;
    private static Properties application = null;

    private ApplicationPropertiesLoader() {}

    public static ApplicationPropertiesLoader getInstance() {
        if (instance == null) {
            instance = new ApplicationPropertiesLoader();
        }
        return instance;
    }

    protected static Properties loadApplicationProperties() {
        Optional<Properties> applicationOpt = PropertiesLoader.loadProperties(AppConstants.APPLICATION_PROPERTIES);
        if (applicationOpt.isPresent()) {
            Properties defaultApplication = PropertiesLoader.loadDefaultProperties(AppConstants.APPLICATION_PROPERTIES);
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
        return PropertiesLoader.loadDefaultProperties(AppConstants.APPLICATION_PROPERTIES);
    }

    protected Properties getApplicationProperties() {
        if (application == null) {
            application = loadApplicationProperties();
        }
        return application;
    }

    public String getUrl(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.URL.key(), defaultValue);
    }

    public Integer getLimit(int defaultValue) {
        return Integer.valueOf(getApplicationProperties().getProperty(SettingKeys.LIMIT.key(), String.valueOf(defaultValue)));
    }

    public Integer getPage(int defaultValue) {
        return Integer.valueOf(getApplicationProperties().getProperty(SettingKeys.PAGE.key(), String.valueOf(defaultValue)));
    }

    public String getFilePath(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.FILE_PATH.key(), defaultValue);
    }

    public String getUsername() {
        return getApplicationProperties().getProperty(SettingKeys.USERNAME.key());
    }

    public String getPassword() {
        return getApplicationProperties().getProperty(SettingKeys.PASSWORD.key());
    }

    public String getServerUrl() {
        return getApplicationProperties().getProperty(SettingKeys.SERVER_URL.key());
    }

    public Integer getServerPort(int defaultValue) {
        return Integer.valueOf(getApplicationProperties().getProperty(SettingKeys.SERVER_PORT.key(), String.valueOf(defaultValue)));
    }

    public String getApiInfo() {
        return getApplicationProperties().getProperty(SettingKeys.API_INFO.key());
    }

    public String getDestination() {
        return getApplicationProperties().getProperty(SettingKeys.DESTINATION.key());
    }

    public String getTorrentUrlType(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.TORRENT_URL_TYPE.key(), defaultValue);
    }

    public String getCreationMethod(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.CREATION_METHOD.key(), defaultValue);
    }

    public String getTorrentLocation(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.TORRENT_LOCATION.key());
    }

    public Integer getTorrentAge(int defaultValue) {
        return Integer.valueOf(getApplicationProperties().getProperty(SettingKeys.TORRENT_AGE.key(), String.valueOf(defaultValue)));
    }

    public String getRepeatDownload(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.REPEAT_DOWNLOAD.key(), defaultValue);
    }

    public void extractUsernameFromArgs(String[] args) {
        if (getApplicationProperties().containsKey(SettingKeys.USERNAME.key())) {
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
            getApplicationProperties().setProperty(SettingKeys.USERNAME.key(),
                    usernameArg.substring(usernameArg.lastIndexOf("=") + 1));
        } else {
            throw new IllegalArgumentException("No userName where given. Add username to application.properties " +
                    "or run program with username param (username=login).");
        }
    }

    public void extractPasswordFromArgs(String[] args) {
        if (getApplicationProperties().containsKey(SettingKeys.PASSWORD.key())) {
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
            getApplicationProperties().setProperty(SettingKeys.PASSWORD.key(), pass.substring(pass.lastIndexOf("=") + 1));
        } else {
            throw new IllegalArgumentException("No password where given. Add password to application.properties " +
                    "or run program with passwd param (passwd=somePass).");
        }
    }

}
