package pg.ui.window.controller.task.atomic.call.ds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.props.ApplicationPropertiesHelper;
import pg.web.ds.DSAllowedProtocol;

/** Created by Gawa 2017-11-11 */
public class BasicCall {

    protected final Logger logger;
    protected final ApplicationPropertiesHelper application;
    private static String serverUrl;

    public BasicCall() {
        this.logger = LogManager.getLogger(this.getClass());
        this.application = ApplicationPropertiesHelper.getInstance();
    }

    protected String prepareServerUrl() {
        if (serverUrl != null) {
            return serverUrl;
        }
        String server = application.getServerUrl();
        if (server != null && !server.isEmpty()) {
            DSAllowedProtocol protocol = application.getServerPort(DSAllowedProtocol.https);
            return serverUrl = String.format("%s://%s:%s", protocol.name(), server, protocol.port());
        }
        return serverUrl = "";
    }
}
