package pg;

import javafx.application.Application;
import javafx.stage.Stage;
import pg.ui.checker.ApplicationChecker;
import pg.ui.checker.Checker;
import pg.ui.checker.ShowChecker;
import pg.ui.window.WindowHandler;
import pg.ui.window.WindowHandlerImpl;

/** Created by Gawa 2017-10-04*/
public class FXMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        WindowHandler windowHandler = new WindowHandlerImpl(primaryStage);
        Checker checker = new ApplicationChecker();
        if (!checker.ready()) {
            windowHandler.launchConfigWindow();
        }
        checker = new ShowChecker();
        if (!checker.ready()) {
            windowHandler.launchShowWindow();
        }
        windowHandler.launchMainWindow();
    }
}
