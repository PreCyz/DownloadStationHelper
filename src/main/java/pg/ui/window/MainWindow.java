package pg.ui.window;

import pg.ui.controller.AbstractController;

import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
public class MainWindow extends AbstractWindow {

    public MainWindow(AbstractController controller, ResourceBundle bundle) {
        super(controller, bundle);
    }

    @Override
    protected String fxmlFileName() {
        return "main.fxml";
    }

    @Override
    protected String windowImgFileName() {
        return "mainImg.png";
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
