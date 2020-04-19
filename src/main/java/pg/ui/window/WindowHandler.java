package pg.ui.window;

import javafx.stage.Window;
import pg.exceptions.ProgramException;
import pg.web.ds.detail.DsApiDetail;

/**Created by Gawa 2017-10-04*/
public interface WindowHandler {
    void launchConfigWindow();
    void setDsApiDetail(DsApiDetail dsApiDetail);
    DsApiDetail getDsApiDetail();
    void launchMainWindow();
    void launchShowWindow();
    void launchSearchWindow();
    Window currentWindow();
    void handleException(ProgramException exception);
    void logoutOnExit();
}
