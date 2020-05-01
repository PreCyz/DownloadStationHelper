package pg.props;

import com.google.common.io.CharStreams;
import org.slf4j.LoggerFactory;
import pg.program.SettingKeys;
import pg.program.StartParameters;
import pg.util.AppConstants;
import pg.util.CryptoUtils;
import pg.web.ds.DSAllowedProtocol;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by Gawa on 12/09/17.
 */
public final class ApplicationPropertiesHelper {

    private static Properties application = null;
    private static final String NOT_AVAILABLE = "N/A";
    private static String version = NOT_AVAILABLE;

    private ApplicationPropertiesHelper() {
    }

    private static class ApplicationPropertiesHelperHolder {
        private static final ApplicationPropertiesHelper INSTANCE = new ApplicationPropertiesHelper();
    }

    public synchronized static ApplicationPropertiesHelper getInstance() {
        return ApplicationPropertiesHelperHolder.INSTANCE;
    }

    protected Properties loadApplicationProperties(String fileName) {
        Optional<Properties> applicationOpt = PropertiesHelper.loadProperties(fileName);
        if (applicationOpt.isPresent()) {
            Properties defaultApplication = PropertiesHelper.loadDefaultProperties(fileName);
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
        return PropertiesHelper.loadDefaultProperties(fileName);
    }

    protected synchronized Properties getApplicationProperties() {
        if (application == null) {
            application = loadApplicationProperties(AppConstants.APPLICATION_PROPERTIES);
        }
        return application;
    }

    public String getAppVersion() {
        if (NOT_AVAILABLE.equals(version)) {

            version = getVersionFromJar();

            if (NOT_AVAILABLE.equals(version)) {
                version = getVersionFromSettings();
            }
        }
        return version;
    }

    private String getVersionFromJar() {
        String result = NOT_AVAILABLE;
        try (InputStream jarVersionIs = getClass().getClassLoader().getResourceAsStream(AppConstants.VERSION_TXT);
             final Reader jarReader = new InputStreamReader(jarVersionIs);
        ) {
            result = CharStreams.toString(jarReader);
        } catch (IOException ex) {
            LoggerFactory.getLogger(ApplicationPropertiesHelper.class).warn("Can not read '{}'.", AppConstants.VERSION_TXT);
        }
        return result;
    }

    private String getVersionFromSettings() {
        String result = NOT_AVAILABLE;
        try (final InputStream settingsVersionIs = new FileInputStream(AppConstants.VERSION_PATH.toString());
             final Reader settingsReader = new InputStreamReader(settingsVersionIs);
        ) {
            result = CharStreams.toString(settingsReader);
        } catch (IOException e) {
            LoggerFactory.getLogger(ApplicationPropertiesHelper.class)
                    .warn("Can not read '{}'.", AppConstants.VERSION_PATH.toString());
        }
        return result;
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
        return getApplicationProperties().getProperty(SettingKeys.RESULT_LOCATION.key(), defaultValue);
    }

    public String getUsername() {
        return getApplicationProperties().getProperty(SettingKeys.USERNAME.key());
    }

    public String getPassword() {
        String encryptedPassword = getApplicationProperties().getProperty(SettingKeys.PASSWORD.key());
        if (encryptedPassword != null) {
            try {
                return CryptoUtils.decrypt(encryptedPassword);
            } catch (GeneralSecurityException e) {
                LoggerFactory.getLogger(ApplicationPropertiesHelper.class).warn("Can not decrypt password.", e);
            }
        }
        return encryptedPassword;
    }

    public String getServerUrl() {
        return getApplicationProperties().getProperty(SettingKeys.SERVER_URL.key());
    }

    public DSAllowedProtocol getServerPort(DSAllowedProtocol defaultValue) {
        return DSAllowedProtocol.valueFor(Integer.parseInt(getApplicationProperties()
                .getProperty(SettingKeys.SERVER_PORT.key(), String.valueOf(defaultValue.port()))));
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

    public String getTorrentLocation() {
        return getApplicationProperties().getProperty(SettingKeys.TORRENT_LOCATION.key());
    }

    public Integer getTorrentAge(int defaultValue) {
        return Integer.valueOf(getApplicationProperties().getProperty(SettingKeys.TORRENT_AGE_DAYS.key(), String.valueOf(defaultValue)));
    }

    public String getRepeatDownload(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.REPEAT_DOWNLOAD.key(), defaultValue);
    }

    public String getHandleDuplicates(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.HANDLE_DUPLICATES.key(), defaultValue);
    }

    public String getMaxFileSize(String defaultValue) {
        return getApplicationProperties().getProperty(SettingKeys.MAX_FILE_SIZE.key(), defaultValue).trim().replace(",", ".");
    }

    public String getTorrentReleaseDate() {
        return getApplicationProperties().getProperty(SettingKeys.TORRENT_RELEASE_DATE.key(), "");
    }

    public Integer getApiVersion() {
        return Integer.valueOf(getApplicationProperties().getProperty(SettingKeys.API_VERSION.key(), "0"));
    }

    public long getLiveTrackInterval() {
        return Long.parseLong(getApplicationProperties().getProperty(SettingKeys.LIVE_TRACK.key(), "0"));
    }

    public long getSearchLimit() {
        return Long.parseLong(getApplicationProperties().getProperty(SettingKeys.SEARCH_LIMIT.key(), "10"));
    }

    public void extractUsername(String[] args) {
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

    public void store(ConfigBuilder configBuilder) throws IOException {
        storeApplicationProperties(configBuilder.createConfig());
    }

    private synchronized void storeApplicationProperties(Properties config) throws IOException {
        PropertiesHelper.storeApplicationProperties(config);
        application = null;
    }

    public void storeApiVersion(String apiVersion) throws IOException {
        application.setProperty(SettingKeys.API_VERSION.key(), apiVersion);
        storeApplicationProperties(application);
    }
}
