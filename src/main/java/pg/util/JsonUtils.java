package pg.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**Created by Gawa on 15/08/17.*/
public class JsonUtils {

    private static final String NOTHING_TO_SHOW = "Nothing to show";

    private JsonUtils() {}

    public static <T> Optional<T> convertFromString(String json, Class<T> type) {
        try {
            return Optional.of(new ObjectMapper().readValue(json, type));
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    public static <T> Optional<T> convertFromFile(String jsonFileName, Class<T> clazz) {
        URL resource = JsonUtils.class.getClassLoader().getResource(jsonFileName);
        if (resource == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new ObjectMapper().readValue(resource, clazz));
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    public static void writeToFile(Path filePath, Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.writeValue(filePath.toFile(), object);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
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
            System.out.println(e.getLocalizedMessage());
        }
        return NOTHING_TO_SHOW;
    }
}
