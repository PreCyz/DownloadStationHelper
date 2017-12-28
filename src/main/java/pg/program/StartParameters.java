package pg.program;

/**Created by Gawa on 27/08/17.*/
public enum StartParameters {
    USERNAME("username"),
    PASSWORD("passwd"),
    IMDB_MODE("imdbMode");

    private String param;

    StartParameters(String param) {
        this.param = param;
    }

    public String param() {
        return param;
    }
}
