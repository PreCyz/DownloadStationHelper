package pg.web.synology;

/**Created by Gawa on 25/08/17.*/
public enum AuthMethod {
    LOGIN("login"),
    LOGOUT("logout");

    private String method;

    AuthMethod(String method) {
        this.method = method;
    }

    public String method() {
        return method;
    }
}
