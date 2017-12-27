package pg.ui.window.controller;

import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.ui.handler.WindowHandler;

import java.net.URL;
import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
public abstract class AbstractController implements Initializable {

    protected final Logger logger;
    protected final WindowHandler windowHandler;
    protected URL location;
    protected ResourceBundle bundle;

    protected AbstractController(WindowHandler windowHandler) {
        this.windowHandler = windowHandler;
        this.logger = LogManager.getLogger(getClass());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.location = location;
        bundle = resources;
    }
}
