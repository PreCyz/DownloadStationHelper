package pg.ui.window;

import pg.ui.controller.AbstractController;

import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
public class ConfigWindow extends AbstractWindow {

    public ConfigWindow(AbstractController controller, ResourceBundle bundle) {
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
