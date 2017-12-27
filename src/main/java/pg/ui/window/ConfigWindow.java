package pg.ui.window;

import pg.ui.window.controller.AbstractController;

import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
class ConfigWindow extends AbstractWindow {

    ConfigWindow(AbstractController controller, ResourceBundle bundle) {
        super(controller, bundle);
    }

    @Override
    protected String fxmlFileName() {
        return "config.fxml";
    }

    @Override
    protected String windowImgFileName() {
        return "configImg.png";
    }

    @Override
    protected String cssFileName() {
        return "config.css";
    }

    @Override
    public String windowTitleBundle() {
        return "config.title";
    }
}
