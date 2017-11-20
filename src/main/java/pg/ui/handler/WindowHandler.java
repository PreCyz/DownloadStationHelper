package pg.ui.handler;

import javafx.stage.Window;
import pg.ui.exception.ProgramException;
import pg.ui.task.atomic.call.ds.DsApiDetail;

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
