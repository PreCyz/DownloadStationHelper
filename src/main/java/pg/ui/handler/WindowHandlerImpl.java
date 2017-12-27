package pg.ui.handler;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.ui.ResourceHelper;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.ui.task.atomic.call.ds.LogoutCall;
import pg.ui.window.AbstractWindow;
import pg.ui.window.WindowFactory;
import pg.web.response.detail.DsApiDetail;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static pg.util.AppConstants.RESOURCE_BUNDLE;

/**Created by Gawa 2017-10-04*/
public class WindowHandlerImpl implements WindowHandler {

    private static final Logger logger = LogManager.getLogger(WindowHandlerImpl.class);

    private final Stage primaryStage;
    private ResourceBundle bundle;
    private Window window;
    private DsApiDetail dsApiDetail;
    private boolean isLoggedIn;

    public WindowHandlerImpl(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(onCloseEventHandler());
        bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, Locale.getDefault());
    }

    @Override
    public void setDsApiDetail(DsApiDetail dsApiDetail) {
        this.dsApiDetail = dsApiDetail;
    }

    private EventHandler<WindowEvent> onCloseEventHandler() {
        return t -> {
            if (isLoggedIn) {
                new LogoutCall(dsApiDetail.getAuthInfo()).call();
            } else {
                logger.info("Logout from disk station is not needed.");
            }
            Platform.exit();
            System.exit(0);
        };
    }

    @Override
    public void launchMainWindow() {
        buildScene(primaryStage, WindowFactory.MAIN.createWindow(this, bundle));
        primaryStage.show();
    }

    @Override
    public void launchConfigWindow() {
        Stage configStage = new Stage();
        configStage.initModality(Modality.WINDOW_MODAL);
        buildScene(configStage, WindowFactory.CONFIG.createWindow(this, bundle));
        configStage.showAndWait();
    }

    @Override
    public void launchShowWindow() {
        Stage showStage = new Stage();
        showStage.initModality(Modality.WINDOW_MODAL);
        buildScene(showStage, WindowFactory.SHOW.createWindow(this, bundle));
        showStage.showAndWait();
    }

    private void buildScene(Stage stage, AbstractWindow window) {
        try {
            Image icon = ResourceHelper.readImage(window.windowImgFilePath());
            stage.getIcons().add(icon);
        } catch (ProgramException ex) {
            logger.warn("Problem with loading window icon: {}.", ex.getMessage());
        }
        try {
            stage.setTitle(bundle.getString(window.windowTitleBundle()));
            stage.setResizable(window.resizable());
	        Scene scene = new Scene(window.root());
	        scene.getStylesheets().add(window.css());
	        stage.setScene(scene);
            this.window = stage;
            //window.refreshWindowSize();
        } catch (IOException ex) {
            handleException(new ProgramException(UIError.LAUNCH_PROGRAM, ex.getLocalizedMessage(), ex));
        }
    }

    @Override
    public Window currentWindow() {
        return window;
    }

    /*public void changeWindowWidth(double width) {
        window.setWidth(window.getWidth() - width);
    }*/

    @Override
    public void handleException(ProgramException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.CLOSE);
        alert.setTitle(bundle.getString("program.exception"));
        alert.setHeaderText(bundle.getString("exception.occurred"));
        alert.setContentText(exception.getUiError().msg());

        Label label = new Label(bundle.getString("exception.details"));

        TextArea textArea = new TextArea(exception.getMessage());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    @Override
    public void logoutOnExit() {
        isLoggedIn = true;
    }
}
