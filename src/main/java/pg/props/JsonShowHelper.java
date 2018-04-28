package pg.props;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.ShowDetail;
import pg.util.AppConstants;
import pg.util.JsonUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

public class JsonShowHelper {

    private static final Logger logger = LogManager.getLogger(JsonShowHelper.class);

    private static JsonShowHelper instance;
    private Set<ShowDetail> showDetails;
    private boolean initialized;

    private JsonShowHelper() {
        showDetails = new TreeSet<>(ShowDetail.COMPARATOR);;
        initialized = false;
    }

    public static synchronized JsonShowHelper getInstance() {
        if (instance == null) {
            instance = new JsonShowHelper();
        }
        return instance;
    }

    public Set<ShowDetail> getShowDetails() {
        if (initialized) {
            return showDetails;
        }
        initialized = true;
        showDetails.addAll(JsonUtils.convertJsonToSet(getShowsPath()));
        return showDetails;
    }

    @NotNull
    private Path getShowsPath() {
        return Paths.get(".", AppConstants.SETTINGS, "shows.json");
    }

    public void saveShows(Set<ShowDetail> shows) {
        try {
            showDetails = new TreeSet<>(ShowDetail.COMPARATOR);
            showDetails.addAll(shows);
            JsonUtils.writeToFile(JsonShowHelper.getInstance().getShowsPath(), showDetails);
        } catch (RuntimeException ex) {
            logger.error("Could not save shows.json", ex);
            throw new ProgramException(UIError.SAVE_PROPERTIES, ex);
        }
    }

    public boolean jsonShowsNotExist() {
        return Files.notExists(getShowsPath());
    }
}
