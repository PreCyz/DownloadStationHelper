package pg.services.ds;

import java.util.HashMap;
import java.util.Map;

/** Created by Gawa 2017-11-11 */
public final class DSError {

    private DSError() {}

    private static final Map<Integer, String> loginErrorMap = new HashMap<>();
    private static final Map<Integer, String> authErrorMap = new HashMap<>();
    private static final Map<Integer, String> taskErrorMap = new HashMap<>();
    private static final Map<Integer, String> searchErrorMap = new HashMap<>();

    static {
        loginErrorMap.put(100, "Unknown error");
        loginErrorMap.put(101, "Invalid parameter");
        loginErrorMap.put(102, "The requested API does not exist");
        loginErrorMap.put(103, "The requested method does not exist");
        loginErrorMap.put(104, "The requested version does not support the functionality");
        loginErrorMap.put(105, "The logged in session does not have permission");
        loginErrorMap.put(106, "Session timeout");
        loginErrorMap.put(107, "Session interrupted by duplicate login");

        authErrorMap.putAll(loginErrorMap);
        authErrorMap.put(400, "No such account or incorrect password");
        authErrorMap.put(401, "Account disabled");
        authErrorMap.put(402, "Permission denied");
        authErrorMap.put(403, "2-step verification code required");
        authErrorMap.put(404, "Failed to authenticate 2-step verification code");

        taskErrorMap.putAll(loginErrorMap);
        taskErrorMap.put(400, "File upload failed");
        taskErrorMap.put(401, "Max number of tasks reached");
        taskErrorMap.put(402, "Destination denied");
        taskErrorMap.put(403, "Destination does not exist");
        taskErrorMap.put(404, "Invalid task id");
        taskErrorMap.put(405, "Invalid task action");
        taskErrorMap.put(406, "No default destination");
        taskErrorMap.put(407, "Set destination failed");
        taskErrorMap.put(408, "File does not exist");

        searchErrorMap.putAll(loginErrorMap);
        searchErrorMap.put(400, "Unknown error");
        searchErrorMap.put(401, "Invalid parameter");
        searchErrorMap.put(402, "Parse the user setting failed");
        searchErrorMap.put(403, "Get category failed");
        searchErrorMap.put(404, "Get the search result from DB failed");
        searchErrorMap.put(405, "Get the user setting failed");
    }

    public static String getAuthError(int responseCode) {
        return authErrorMap.get(responseCode);
    }

    public static String getTaskError(int responseCode) {
        return taskErrorMap.get(responseCode);
    }

    public static String getSearchError(int responseCode) {
        return searchErrorMap.get(responseCode);
    }
}
