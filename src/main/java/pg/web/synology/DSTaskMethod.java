package pg.web.synology;

/**Created by Gawa on 26/08/17.*/
public enum DSTaskMethod {
    CREATE("create"),
    DELETE("delete"),
    PAUSE("pause"),
    RESUME("resume"),
    EDIT("edit");

    private String method;

    DSTaskMethod(String method) {
        this.method = method;
    }

    public String method() {
        return method;
    }
}
