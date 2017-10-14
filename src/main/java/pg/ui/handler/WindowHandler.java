package pg.ui.handler;

import javafx.stage.Window;

/**Created by Gawa 2017-10-04*/
public interface WindowHandler {
    void launchConfigWindow();
    void launchMainWindow();
    void launchShowWindow();
    Window currentWindow();
}
