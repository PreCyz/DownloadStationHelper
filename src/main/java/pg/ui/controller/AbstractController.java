package pg.ui.controller;

import javafx.fxml.Initializable;
import pg.ui.handler.WindowHandler;

import java.net.URL;
import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
public abstract class AbstractController implements Initializable {

    protected final WindowHandler windowHandler;
    protected URL location;
    protected ResourceBundle bundle;

    protected AbstractController(WindowHandler windowHandler) {
        this.windowHandler = windowHandler;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.location = location;
        bundle = resources;
    }
}
