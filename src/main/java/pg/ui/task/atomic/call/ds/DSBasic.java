package pg.ui.task.atomic.call.ds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.props.ApplicationPropertiesHelper;
import pg.web.model.AllowedProtocol;

/** Created by Gawa 2017-11-11 */
public class DSBasic {

    protected final Logger logger;
    protected final ApplicationPropertiesHelper application;
    private static String serverUrl;

    public DSBasic() {
        this.logger = LogManager.getLogger(this.getClass());
        this.application = ApplicationPropertiesHelper.getInstance();
    }

    String prepareServerUrl() {
        if (serverUrl != null) {
            return serverUrl;
        }
        String server = application.getServerUrl();
        if (server != null && !server.isEmpty()) {
            AllowedProtocol protocol = application.getServerPort(AllowedProtocol.https);
            return serverUrl = String.format("%s://%s:%s", protocol.name(), server, protocol.port());
        }
        return serverUrl = "";
    }
}
