package pg.ui.checker;

import pg.props.JsonShowHelper;

/**Created by Gawa 2017-10-07*/
public class ShowChecker implements Checker {

    private JsonShowHelper helper;

    public ShowChecker() {
        this.helper = JsonShowHelper.getInstance();
    }

    @Override
    public boolean ready() {
        return !helper.getShowDetails().isEmpty();
    }
}
