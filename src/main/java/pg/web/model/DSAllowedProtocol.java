package pg.web.model;

/**Created by Gawa 2017-10-14*/
public enum DSAllowedProtocol {
    http(5000), https(5001);

    private int port;
    DSAllowedProtocol(int port) {
        this.port = port;
    }

    public int port() {
        return port;
    }

    public static DSAllowedProtocol valueFor(int port) {
        for (DSAllowedProtocol DSAllowedProtocol : DSAllowedProtocol.values()) {
            if (DSAllowedProtocol.port == port) {
                return DSAllowedProtocol;
            }
        }
        return http;
    }
}
