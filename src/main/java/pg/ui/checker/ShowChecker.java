package pg.ui.checker;

import pg.props.ShowsPropertiesHelper;

/**Created by Gawa 2017-10-07*/
public class ShowChecker implements Checker {

    private ShowsPropertiesHelper helper;

    public ShowChecker() {
        this.helper = ShowsPropertiesHelper.getInstance();
    }

    @Override
    public boolean ready() {
        return !helper.keySet().isEmpty();
    }
}
