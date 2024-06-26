package pg.ui.window;

import pg.ui.window.controller.AbstractController;

import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
class MainWindow extends AbstractWindow {

    MainWindow(AbstractController controller, ResourceBundle bundle) {
        super(controller, bundle);
    }

    @Override
    protected String fxmlFileName() {
        return "main.fxml";
    }

    @Override
    protected String windowImgFileName() {
        return "main.png";
    }

    @Override
    protected String cssFileName() {
        return "main.css";
    }

    @Override
    public String windowTitleBundle() {
        return "main.title";
    }
}
