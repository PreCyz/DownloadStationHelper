package pg.ui.window;

import pg.ui.window.controller.ConfigController;
import pg.ui.window.controller.MainControllerCompletable;
import pg.ui.window.controller.SearchController;
import pg.ui.window.controller.ShowController;

import java.util.ResourceBundle;

/**Created by Gawa 2017-10-04*/
public enum WindowFactory {
    MAIN {
        @Override
        public AbstractWindow createWindow(WindowHandler windowHandler, ResourceBundle bundle) {
            return new MainWindow(new MainControllerCompletable(windowHandler), bundle);
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
    },SEARCH {
        @Override
        public AbstractWindow createWindow(WindowHandler windowHandler, ResourceBundle bundle) {
            return new SearchWindow(new SearchController(windowHandler), bundle);
        }
    };

    public abstract AbstractWindow createWindow(WindowHandler windowHandler, ResourceBundle bundle);
}
