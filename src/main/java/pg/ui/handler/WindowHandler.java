package pg.ui.handler;

import javafx.stage.Window;
import pg.exception.ProgramException;
import pg.web.response.detail.DsApiDetail;

/**Created by Gawa 2017-10-04*/
public interface WindowHandler {
    void launchConfigWindow();
    void setDsApiDetail(DsApiDetail dsApiDetail);
    void launchMainWindow();
    void launchShowWindow();
    Window currentWindow();
    void handleException(ProgramException exception);

    void logoutOnExit();
}
