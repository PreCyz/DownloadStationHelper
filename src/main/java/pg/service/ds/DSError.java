package pg.service.ds;

import java.util.HashMap;
import java.util.Map;

/** Created by Gawa 2017-11-11 */
public final class DSError {

    private DSError() {}

    private static Map<Integer, String> authErrorMap = new HashMap<>();
    private static Map<Integer, String> taskErrorMap = new HashMap<>();

    static {
        authErrorMap.put(100, "Unknown error");
        authErrorMap.put(101, "Invalid parameter");
        authErrorMap.put(102, "The requested API does not exist");
        authErrorMap.put(103, "The requested method does not exist");
        authErrorMap.put(104, "The requested version does not support the functionality");
        authErrorMap.put(105, "The logged in session does not have permission");
        authErrorMap.put(106, "Session timeout");
        authErrorMap.put(107, "Session interrupted by duplicate login");
        authErrorMap.put(400, "No such account or incorrect password");
        authErrorMap.put(401, "Account disabled");
        authErrorMap.put(402, "Permission denied");
        authErrorMap.put(403, "2-step verification code required");
        authErrorMap.put(404, "Failed to authenticate 2-step verification code");

        taskErrorMap.put(100, "Unknown error");
        taskErrorMap.put(101, "Invalid parameter");
        taskErrorMap.put(102, "The requested API does not exist");
        taskErrorMap.put(103, "The requested method does not exist");
        taskErrorMap.put(104, "The requested version does not support the functionality");
        taskErrorMap.put(105, "The logged in session does not have permission");
        taskErrorMap.put(106, "Session timeout");
        taskErrorMap.put(107, "Session interrupted by duplicate login");
        taskErrorMap.put(400, "File upload failed");
        taskErrorMap.put(401, "Max number of tasks reached");
        taskErrorMap.put(402, "Destination denied");
        taskErrorMap.put(403, "Destination does not exist");
        taskErrorMap.put(404, "Invalid task id");
        taskErrorMap.put(405, "Invalid task action");
        taskErrorMap.put(406, "No default destination");
        taskErrorMap.put(407, "Set destination failed");
        taskErrorMap.put(408, "File does not exist");
    }

    public static String getAuthError(int responseCode) {
        return authErrorMap.get(responseCode);
    }

    public static String getTaskError(int responseCode) {
        return taskErrorMap.get(responseCode);
    }
}
