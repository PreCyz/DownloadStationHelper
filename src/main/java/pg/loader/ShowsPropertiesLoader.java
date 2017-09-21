package pg.loader;

import pg.util.AppConstants;

import java.util.Optional;
import java.util.Properties;

/**Created by Gawa on 9/18/2017.*/
public final class ShowsPropertiesLoader {

    private static ShowsPropertiesLoader instance;
    private static Properties shows = null;

    private ShowsPropertiesLoader() {}

    public static ShowsPropertiesLoader getInstance() {
        if (instance == null) {
            instance = new ShowsPropertiesLoader();
        }
        return instance;
    }

    public Properties getShowsProperties() {
        if (shows == null) {
            shows = loadShowsProperties();
        }
        return shows;
    }

    protected static Properties loadShowsProperties() {
        Optional<Properties> showsOpt = PropertiesLoader.loadProperties(AppConstants.SHOWS_PROPERTIES);
        return showsOpt.orElseGet(() -> PropertiesLoader.loadDefaultProperties(AppConstants.SHOWS_PROPERTIES));
    }

    public String getBaseWords(int showNumber) {
        String key = String.format("show.%d.baseWords", showNumber);
        return getShowsProperties().getProperty(key);
    }

    public Integer getMatchPrecision(int showNumber, int defaultValue) {
        String key = String.format("show.%d.matchPrecision", showNumber);
        return Integer.valueOf(getShowsProperties().getProperty(key, String.valueOf(defaultValue)));
    }

    public String getMatchPrecision(int showNumber) {
        String key = String.format("show.%d.imdbId", showNumber);
        return getShowsProperties().getProperty(key);
    }
}
