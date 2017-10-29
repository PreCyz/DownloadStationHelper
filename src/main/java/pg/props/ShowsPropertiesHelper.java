package pg.props;

import pg.util.AppConstants;
import pg.web.model.ShowDetail;

import java.io.IOException;
import java.util.*;

/**Created by Gawa on 9/18/2017.*/
public final class ShowsPropertiesHelper {

    private static ShowsPropertiesHelper instance;
    private static Properties shows = null;

    private ShowsPropertiesHelper() {}

    public static ShowsPropertiesHelper getInstance() {
        if (instance == null) {
            instance = new ShowsPropertiesHelper();
        }
        return instance;
    }

    private Properties getShowsProperties() {
        if (shows == null) {
            shows = loadShowsProperties();
        }
        return shows;
    }

    protected static Properties loadShowsProperties() {
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

    public String getMatchPrecision(int showNumber) {
        String key = String.format("show.%d.imdbId", showNumber);
        return getShowsProperties().getProperty(key);
    }

    public String getProperty(String key) {
        return getShowsProperties().getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return getShowsProperties().getProperty(key, defaultValue);
    }

    public Set<Object> keySet() {
        return getShowsProperties().keySet();
    }

    public Set<ShowDetail> getShowDetails() {
        Set<ShowDetail> showDetails = new TreeSet<>(getShowDetailComparator());
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

    public Comparator<ShowDetail> getShowDetailComparator() {
        return (o1, o2) -> {
                if (o1.getId() > o2.getId()) {
                    return 1;
                }  else if (o1.getId() < o2.getId()) {
                    return -1;
                }
                return 0;
            };
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

    public void prepareAndStore(Set<ShowDetail> showDetails) throws IOException {
        Properties showsToSave = prepareShowProperties(showDetails);
        PropertiesHelper.storeShowProperties(showsToSave);
        instance = null;
        shows = null;
    }

    private Properties prepareShowProperties(Set<ShowDetail> showDetails) {
        Properties shows = new Properties();
        for (ShowDetail showDetail : showDetails) {
            String baseWordKey = String.format("show.%d.baseWords", showDetail.getId());
            shows.put(baseWordKey, showDetail.getBaseWords());
            if (showDetail.getMatchPrecision() != showDetail.getBaseWordsCount()) {
                String matchPrecisionKey = String.format("show.%d.matchPrecision", showDetail.getId());
                shows.put(matchPrecisionKey, String.valueOf(showDetail.getMatchPrecision()));
            }
        }
        return shows;
    }
}
