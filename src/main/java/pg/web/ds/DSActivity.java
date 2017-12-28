package pg.web.ds;

/**Created by Gawa on 25/08/17.*/
public enum DSActivity {
    LOGIN("login"),
    LOGOUT("logout");

    private String method;

    DSActivity(String method) {
        this.method = method;
    }

    public String method() {
        return method;
    }
}
