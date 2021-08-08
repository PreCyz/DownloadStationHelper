package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**Created by Gawa on 25/08/17.*/
@Getter
public class DSLoginDetails {

    private String account;
    @JsonProperty("device_id")
    private String deviceId;
    @JsonProperty("ik_message")
    private String ikMessage;
    @JsonProperty("is_portal_port")
    private boolean portalPort;
    private String sid;
    @JsonProperty("synotoken")
    private String synoToken;

}
