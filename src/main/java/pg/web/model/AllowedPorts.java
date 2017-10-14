package pg.web.model;

/**Created by Gawa 2017-10-14*/
public enum AllowedPorts {
    HTTP(5000), HTTPS(5001);

    private int port;
    AllowedPorts(int port) {
        this.port = port;
    }

    public int port() {
        return port;
    }
}
