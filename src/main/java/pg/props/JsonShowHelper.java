package pg.props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.exceptions.ProgramException;
import pg.exceptions.UIError;
import pg.program.ShowDetail;
import pg.util.AppConstants;
import pg.util.JsonUtils;

import java.nio.file.Files;
import java.util.Set;
import java.util.TreeSet;

public class JsonShowHelper {

    private static final Logger logger = LoggerFactory.getLogger(JsonShowHelper.class);

    private Set<ShowDetail> showDetails;
    private boolean initialized;

    private static class JsonShowHelperHolder {
        private static final JsonShowHelper INSTANCE = new JsonShowHelper();
    }

    private JsonShowHelper() {
        showDetails = new TreeSet<>(ShowDetail.COMPARATOR);;
        initialized = false;
    }

    public static JsonShowHelper getInstance() {
        return JsonShowHelperHolder.INSTANCE;
    }

    public Set<ShowDetail> getShowDetails() {
        if (initialized) {
            return showDetails;
        }
        initialized = true;
        showDetails.addAll(JsonUtils.convertJsonToSet(AppConstants.SHOWS_JSON_PATH));
        return showDetails;
    }

    public void saveShows(Set<ShowDetail> shows) {
        try {
            showDetails = new TreeSet<>(ShowDetail.COMPARATOR);
            showDetails.addAll(shows);
            JsonUtils.writeToFile(AppConstants.SHOWS_JSON_PATH, showDetails);
        } catch (RuntimeException ex) {
            logger.error("Could not save shows.json", ex);
            throw new ProgramException(UIError.SAVE_PROPERTIES, ex);
        }
    }

    public boolean jsonShowsNotExist() {
        return Files.notExists(AppConstants.SHOWS_JSON_PATH);
    }
}
