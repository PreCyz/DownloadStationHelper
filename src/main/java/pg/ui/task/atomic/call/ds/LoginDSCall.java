package pg.ui.task.atomic.call.ds;

import pg.service.ds.DSError;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.ApiDetails;
import pg.web.model.ApiName;
import pg.web.response.LoginResponse;
import pg.web.synology.AuthMethod;

import java.util.Optional;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-11-11 */
public class LoginDSCall extends DSBasic implements Callable<String> {

    private final ApiDetails authInfo;
    private String sid;

    public LoginDSCall(ApiDetails authInfo) {
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
            Optional<LoginResponse> jsonResponse =
                    JsonUtils.convertFromString(response.get(), LoginResponse.class);
            logger.info("Login format sid.");
            if (jsonResponse.isPresent()) {
                LoginResponse loginResponse = jsonResponse.get();
                if (loginResponse.isSuccess()) {
                    sid = loginResponse.getLoginDetails().getSid();
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
                        new IllegalArgumentException("Login unsuccessful. No response from server."));
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
                "method=" + AuthMethod.LOGIN.method() + "&" +
                "account=" + userName + "&" +
                "passwd=" + password + "&" +
                "session=" + "DownloadStation" + "&" +
                "format=" + "sid";
    }
}
