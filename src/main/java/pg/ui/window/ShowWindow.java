package pg.ui.window;

import pg.ui.controller.AbstractController;

import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
public class ShowWindow extends AbstractWindow {

    public ShowWindow(AbstractController controller, ResourceBundle bundle) {
        super(controller, bundle);
    }

    @Override
    protected String fxmlFileName() {
        return "show.fxml";
    }

    @Override
    protected String windowImgFileName() {
        return "showImg.png";
    }

    @Override
    protected String cssFileName() {
        return "show.css";
    }

    @Override
    public String windowTitleBundle() {
        return "Shows configuration";
    }
}