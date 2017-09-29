package pg.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**Created by Gawa on 15/08/17.*/
public class JsonUtils {

    private static final Logger logger = LogManager.getLogger(JsonUtils.class);

    private static final String NOTHING_TO_SHOW = "Nothing to show";

    private JsonUtils() {}

    public static <T> Optional<T> convertFromString(String json, Class<T> type) {
        try {
            return Optional.of(new ObjectMapper().readValue(json, type));
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    public static <T> Optional<T> convertFromFile(Path jsonPath, Class<T> clazz) {
        try {
            return Optional.of(new ObjectMapper().readValue(jsonPath.toFile(), clazz));
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    public static void writeToFile(Path filePath, Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.writeValue(filePath.toFile(), object);
            logger.info("File {} was written to '{}'.", filePath.getFileName(), filePath.toAbsolutePath());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    public static Date dateFromLong(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.getTime();
    }

    public static String convertToString(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return NOTHING_TO_SHOW;
    }
}
