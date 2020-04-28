package pg.ui.window.controller.task.atomic.call.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.props.ApplicationPropertiesHelper;
import pg.web.ds.DSAllowedProtocol;

/** Created by Gawa 2017-11-11 */
class BasicCall {

    protected final Logger logger;
    protected final ApplicationPropertiesHelper application;
    private static String serverUrl;

    BasicCall() {
        this.logger = LoggerFactory.getLogger(this.getClass());
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
