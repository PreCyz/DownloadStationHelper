package pg.ui.window.controller.task.atomic.call.ds;

import pg.program.ApiName;
import pg.services.ds.DSError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.DSActivity;
import pg.web.ds.DSGeneralResponse;
import pg.web.ds.detail.DSApiDetails;

import java.util.Optional;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-11-11 */
public class LogoutCall extends BasicCall implements Callable<Void> {

    private DSApiDetails authInfo;

    public LogoutCall(DSApiDetails authInfo) {
        super();
        this.authInfo = authInfo;
    }

    @Override
    public Void call() {
        logoutFromDiskStation();
        return null;
    }

    private void logoutFromDiskStation() {
        String serverUrl = prepareServerUrl();
        if (serverUrl.isEmpty()) {
            logger.info("Server URL not specified.");
        } else {
            String requestUrl = buildLogoutUrl(serverUrl);
            GetClient client = new GetClient(requestUrl);
            Optional<String> response = client.get();
            if (response.isPresent()) {
                Optional<DSGeneralResponse> jsonResponse =
                        JsonUtils.convertFromString(response.get(), DSGeneralResponse.class);
                if (jsonResponse.isPresent()) {
                    DSGeneralResponse logoutResponse = jsonResponse.get();
                    if (logoutResponse.isSuccess()) {
                        logger.info("Logout finished.");
                    } else {
                        String logMsg = String.format("Logout with error %d - %s.",
                                logoutResponse.getError().getCode(),
                                DSError.getAuthError(logoutResponse.getError().getCode()));
                        throw new IllegalArgumentException(logMsg);
                    }
                } else {
                    throw new IllegalArgumentException("Logout with error. No details.");
                }
            }
        }
    }

    private String buildLogoutUrl(String serverUrl) {
        return serverUrl + "/webapi/" + authInfo.getPath() +
                "?" +
                "api=" + ApiName.API_AUTH + "&" +
                "version=" + authInfo.getMaxVersion() + "&" +
                "method=" + DSActivity.LOGOUT.method() + "&" +
                "session=" + "DownloadStation" + "&" +
                "format=" + "sid";
    }
}
