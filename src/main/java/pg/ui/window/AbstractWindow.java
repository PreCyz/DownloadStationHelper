package pg.ui.window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import pg.ui.window.controller.AbstractController;
import pg.util.AppConstants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
public abstract class AbstractWindow {

    private final AbstractController controller;
    private final ResourceBundle bundle;

    AbstractWindow(AbstractController controller, ResourceBundle bundle) {
        this.controller = controller;
        this.bundle = bundle;
    }

    public Parent root() throws IOException {
        FXMLLoader loader = new FXMLLoader(url(), bundle);
        loader.setController(controller);
        return loader.load();
    }

    private URL url() {
        return getClass()
                .getClassLoader()
                .getResource(AppConstants.FXML_RESOURCE_PATH + fxmlFileName());
    }

    public String windowImgFilePath() {
        return AppConstants.IMG_RESOURCE_PATH + windowImgFileName();
    }

    public boolean resizable() {
        return false;
    }

    public String css() {
        return getClass()
                .getClassLoader()
                .getResource(AppConstants.CSS_RESOURCE_PATH + cssFileName())
                .toExternalForm();
    }

    protected abstract String fxmlFileName();
    protected abstract String windowImgFileName();
    protected abstract String cssFileName();
    public abstract String windowTitleBundle();
}
