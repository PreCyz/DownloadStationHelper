package pg.ui.handler;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import pg.factory.WindowFactory;
import pg.ui.window.AbstractWindow;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static pg.util.AppConstants.RESOURCE_BUNDLE;

/**Created by Gawa 2017-10-04*/
public class WindowHandlerImpl implements WindowHandler {

    private Stage primaryStage;
    private ResourceBundle bundle;
    private Window window;

    public WindowHandlerImpl(Stage primaryStage) {
        this.primaryStage = primaryStage;
        bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, Locale.getDefault());
        window = primaryStage;
    }

    @Override
    public void launchMainWindow() {
        buildScene(primaryStage, WindowFactory.MAIN.createWindow(this, bundle));
    }

    @Override
    public void launchConfigWindow() {
        Stage configStage = new Stage();
        configStage.initModality(Modality.WINDOW_MODAL);
        buildScene(configStage, WindowFactory.CONFIG.createWindow(this, bundle));
    }

    @Override
    public void launchShowWindow() {
        Stage showStage = new Stage();
        showStage.initModality(Modality.WINDOW_MODAL);
        buildScene(showStage, WindowFactory.SHOW.createWindow(this, bundle));
    }

    private void buildScene(Stage stage, AbstractWindow window) {
        try {
            /*Image icon = resourceHelper.readImage(window.windowImgFilePath());
            stage.getIcons().add(icon);*/
        } catch (Exception ex) {
            //logger.log(messageHelper.getErrorMsg(ex.getErrorCode()));
            //String errorMsg = MessageHelper.getInstance(bundle).getErrorMsg(ex.getErrorCode(), ex.getArgument());
            //AbstractLogger.addMessage(errorMsg);
        }
        try {
            stage.setTitle(bundle.getString(window.windowTitleBundle()));
            stage.setResizable(window.resizable());
	        Scene scene = new Scene(window.root());
	        scene.getStylesheets().add(window.css());
	        stage.setScene(scene);
            stage.show();
            //window.refreshWindowSize();
        } catch (IOException ex) {
            //handleException(new ProgramException(ErrorCode.LAUNCH_PROGRAM, ex));
        }
    }

    public void changeWindowWidth(double width) {
        window.setWidth(window.getWidth() - width);
    }

    private void handleException(Exception exception) {
        /*MessageHelper messageHelper = MessageHelper.getInstance(ResourceBundle.getBundle(
                RESOURCE_BUNDLE, Locale.getDefault()));
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(messageHelper.getFullMessage("alert.title", Alert.AlertType.ERROR));
        alert.setHeaderText(messageHelper.getFullMessage("alert.header.text"));
        alert.setContentText(messageHelper.getErrorMsg(exception.getErrorCode()));
        alert.setWidth(1000);
        alert.setWidth(750);

        Label label = new Label(messageHelper.getFullMessage("alert.exceptionDetails.label"));

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

        alert.showAndWait();*/
    }
}
