package pg.util;

/**Created by Gawa 2017-09-14*/
public final class StringUtils {

    private StringUtils() {}

    public static boolean nullOrTrimEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean booleanFromString(String value) {
        return "Y".equals(value) || "yes".equals(value) || "true".equals(value) || "T".equals(value);
    }

    public static String stringFromBoolean(boolean value) {
        return value ? "Y" : "N";
    }
}
