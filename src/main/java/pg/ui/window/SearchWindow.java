package pg.ui.window;

import pg.ui.window.controller.SearchController;

import java.util.ResourceBundle;

public class SearchWindow extends AbstractWindow {

    public SearchWindow(SearchController searchController, ResourceBundle bundle) {
        super(searchController, bundle);
    }

    @Override
    protected String fxmlFileName() {
        return "search.fxml";
    }

    @Override
    protected String windowImgFileName() {
        return "searchImg.png";
    }

    @Override
    protected String cssFileName() {
        return "search.css";
    }

    @Override
    public String windowTitleBundle() {
        return "search.title";
    }
}
