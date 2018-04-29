package pg;

import javafx.application.Application;
import javafx.stage.Stage;
import pg.converter.PropertiesToShowDetailsConverter;
import pg.program.ShowDetail;
import pg.props.JsonShowHelper;
import pg.props.ShowsPropertiesHelper;
import pg.ui.checker.ApplicationChecker;
import pg.ui.checker.Checker;
import pg.ui.checker.ShowChecker;
import pg.ui.window.WindowHandler;
import pg.ui.window.WindowHandlerImpl;
import pg.util.AppConstants;

import java.nio.file.Files;
import java.util.Set;

/** Created by Gawa 2017-10-04*/
public class FXMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        upgradePropertiesToJson();

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

    private void upgradePropertiesToJson() {
        if (Files.exists(AppConstants.SHOWS_PROPERTIES_PATH) && Files.notExists(AppConstants.SHOWS_JSON_PATH)) {
            Set<ShowDetail> showDetails = new PropertiesToShowDetailsConverter()
                    .convert(ShowsPropertiesHelper.loadShowsProperties());
            JsonShowHelper.getInstance().saveShows(showDetails);
        }
    }
}
