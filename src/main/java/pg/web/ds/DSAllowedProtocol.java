package pg.web.ds;

/**Created by Gawa 2017-10-14*/
public enum DSAllowedProtocol {
    http(5113), https(5011);

    private final int port;
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
