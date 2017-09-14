package pg.util;

/**Created by Gawa 2017-09-14*/
public final class StringUtils {

    private StringUtils() {}

    public static boolean nullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

}
