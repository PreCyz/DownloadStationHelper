package pg.web.model;

/**Created by Gawa on 27/08/17.*/
public enum StartParameters {
    USERNAME("username"),
    PASSWORD("passwd");

    private String param;

    StartParameters(String param) {
        this.param = param;
    }

    public String param() {
        return param;
    }
}
