package pg.web.response.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Gawa on 25/08/17.
 */
public class LoginDetails {
    @JsonProperty("is_portal_port")
    private boolean portalPort;
    private String sid;

    public boolean isPortalPort() {
        return portalPort;
    }

    public String getSid() {
        return sid;
    }
}
