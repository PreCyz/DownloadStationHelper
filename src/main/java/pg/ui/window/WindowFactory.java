package pg.ui.window;

import pg.ui.controller.ConfigController;
import pg.ui.controller.MainController;
import pg.ui.controller.ShowController;
import pg.ui.handler.WindowHandler;

import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
public enum WindowFactory {
    MAIN {
        @Override
        public AbstractWindow createWindow(WindowHandler windowHandler, ResourceBundle bundle) {
            return new MainWindow(new MainController(windowHandler), bundle);
        }
    },
    CONFIG {
        @Override
        public AbstractWindow createWindow(WindowHandler windowHandler, ResourceBundle bundle) {
            return new ConfigWindow(new ConfigController(windowHandler), bundle);
        }
    },
    SHOW {
        @Override
        public AbstractWindow createWindow(WindowHandler windowHandler, ResourceBundle bundle) {
            return new ShowWindow(new ShowController(windowHandler), bundle);
        }
    };

    public abstract AbstractWindow createWindow(WindowHandler windowHandler, ResourceBundle bundle);
}
