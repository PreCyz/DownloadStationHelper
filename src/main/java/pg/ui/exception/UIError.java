package pg.ui.exception;

/**Created by Gawa 2017-10-07*/
public enum UIError {
    LOAD_BUNDLE("Can not load bundle."),
    SAVE_PROPERTIES("Can not save properties."),
    BUTTON_IMG("Can not load image for button."),
    LAUNCH_PROGRAM("Problem with starting program."),
    GET_TORRENTS("Problem with getting torrents."),
    LOGIN_DS("Problem with log in to disk station."),
    CREATE_TASK("Problem with creating task."),
    LIST_OF_TASK("Problem with getting list of task from disk station."),
    DELETE_TASK("Problem with deleting task on disk station"),
    IMDB("Problem with downloading by imdb."),
    SHORTCUT("Problem wen shortcut was used."),
    FAVOURITES("Problem with downloading favourites.");

    private String msg;

    UIError(String msg) {
        this.msg = msg;
    }

    public String msg() {
        return msg;
    }
}
