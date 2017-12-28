package pg.ui.window.controller.task.atomic.call.ds;

import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.service.ds.DSError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.DSLoginResponse;
import pg.web.ds.detail.DSApiDetails;
import pg.web.model.ApiName;
import pg.web.synology.DSActivity;

import java.util.Optional;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-11-11 */
public class LoginCall extends BasicCall implements Callable<String> {

    private final DSApiDetails authInfo;
    private String sid;

    public LoginCall(DSApiDetails authInfo) {
        super();
        this.authInfo = authInfo;
    }

    @Override
    public String call() {
        login();
        return sid;
    }

    private void login() {
        String requestUrl = buildLoginUrl();
        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            Optional<DSLoginResponse> jsonResponse =
                    JsonUtils.convertFromString(response.get(), DSLoginResponse.class);
            logger.info("Login format sid.");
            if (jsonResponse.isPresent()) {
                DSLoginResponse loginResponse = jsonResponse.get();
                if (loginResponse.isSuccess()) {
                    sid = loginResponse.getDSLoginDetails().getSid();
                    String logMsg = String.format("Login successful. sid = %s.", sid);
                    logger.info(logMsg);
                } else {
                    String logMsg = String.format("Login unsuccessful. Details %d - %s.",
                            loginResponse.getError().getCode(),
                            DSError.getAuthError(loginResponse.getError().getCode()));
                    throw new ProgramException(UIError.LOGIN_DS, new IllegalArgumentException(logMsg));
                }
            } else {
                throw new ProgramException(UIError.LOGIN_DS,
                        new IllegalArgumentException("Login unsuccessful. No ds from server."));
            }
        }
    }

    private String buildLoginUrl() {
        String userName = application.getUsername();
        String password = application.getPassword();
        return prepareServerUrl() + "/webapi/" + authInfo.getPath() +
                "?" +
                "api=" + ApiName.API_AUTH + "&" +
                "version=" + authInfo.getMaxVersion() + "&" +
                "method=" + DSActivity.LOGIN.method() + "&" +
                "account=" + userName + "&" +
                "passwd=" + password + "&" +
                "session=" + "DownloadStation" + "&" +
                "format=" + "sid";
    }
}
