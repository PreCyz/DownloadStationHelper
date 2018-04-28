package pg.props;

import pg.program.ShowDetail;
import pg.util.AppConstants;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**Created by Gawa on 9/18/2017.*/
@Deprecated
public final class ShowsPropertiesHelper {

    private static ShowsPropertiesHelper instance;
    private static Properties shows = null;

    private ShowsPropertiesHelper() {}

    public static synchronized ShowsPropertiesHelper getInstance() {
        if (instance == null) {
            instance = new ShowsPropertiesHelper();
        }
        return instance;
    }

    private synchronized Properties getShowsProperties() {
        if (shows == null) {
            shows = loadShowsProperties();
        }
        return shows;
    }

    public static Properties loadShowsProperties() {
        Optional<Properties> showsOpt = PropertiesHelper.loadProperties(AppConstants.SHOWS_PROPERTIES);
        return showsOpt.orElseGet(() -> PropertiesHelper.loadDefaultProperties(AppConstants.SHOWS_PROPERTIES));
    }

    public String getBaseWords(int showNumber) {
        String key = String.format("show.%d.baseWords", showNumber);
        return getShowsProperties().getProperty(key);
    }

    public Integer getMatchPrecision(int showNumber, int defaultValue) {
        String key = String.format("show.%d.matchPrecision", showNumber);
        return Integer.valueOf(getShowsProperties().getProperty(key, String.valueOf(defaultValue)));
    }

    public String getProperty(String key) {
        return getShowsProperties().getProperty(key);
    }

    public Set<Object> keySet() {
        return getShowsProperties().keySet();
    }

    public Set<ShowDetail> getShowDetails() {
        Set<ShowDetail> showDetails = new TreeSet<>(ShowDetail.COMPARATOR);
        for (Object keyObject : keySet()) {
            String key = String.valueOf(keyObject);
            if (key.endsWith("baseWords")) {
                int id = extractIdFromKey(key);
                String baseWords = getBaseWords(id);
                int matchPrecision = getMatchPrecision(id, baseWords.split(",").length);
                showDetails.add(new ShowDetail(id, baseWords, matchPrecision));
            }
        }
        return showDetails;
    }

    protected int extractIdFromKey(final String key) {
        String reduced = key.replace("show.", "");
        if (key.contains(".baseWords")) {
            reduced = reduced.replace(".baseWords", "");
            return Integer.parseInt(reduced);
        } else if (key.contains(".matchPrecision")) {
            reduced = reduced.replace(".matchPrecision", "");
            return Integer.parseInt(reduced);
        } else if (key.contains(".imdbId")) {
            reduced = reduced.replace(".imdbId", "");
            return Integer.parseInt(reduced);
        }
        return 0;
    }

}
