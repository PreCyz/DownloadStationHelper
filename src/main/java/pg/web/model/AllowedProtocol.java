package pg.web.model;

/**Created by Gawa 2017-10-14*/
public enum AllowedProtocol {
    http(5000), https(5001);

    private int port;
    AllowedProtocol(int port) {
        this.port = port;
    }

    public int port() {
        return port;
    }

    public static AllowedProtocol valueFor(int port) {
        for (AllowedProtocol allowedProtocol : AllowedProtocol.values()) {
            if (allowedProtocol.port == port) {
                return allowedProtocol;
            }
        }
        return http;
    }
}
