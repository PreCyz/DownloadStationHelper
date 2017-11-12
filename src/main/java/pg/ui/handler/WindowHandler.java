package pg.ui.handler;

import javafx.stage.Window;
import pg.ui.exception.ProgramException;
import pg.ui.task.LoginToDSTask;

/**Created by Gawa 2017-10-04*/
public interface WindowHandler {
    void launchConfigWindow();
    void setLoggedInToDs(LoginToDSTask loggedInToDs);
    void launchMainWindow();
    void launchShowWindow();
    Window currentWindow();
    void handleException(ProgramException exception);
}
