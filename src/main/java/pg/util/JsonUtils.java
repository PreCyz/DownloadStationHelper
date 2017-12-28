package pg.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.DSDeleteResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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

    public static Map<String, ReducedDetail> convertMatchTorrentsFromFile(Path jsonPath) {
        try {
            return new ObjectMapper().readValue(jsonPath.toFile(), new TypeReference<Map<String, ReducedDetail>>() {});
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return Collections.emptyMap();
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

    public static List<DSDeleteResponse> convertDeleteResponseFromString(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
            mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
            return mapper.readValue(json, new TypeReference<List<DSDeleteResponse>>(){});
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return Collections.emptyList();
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
