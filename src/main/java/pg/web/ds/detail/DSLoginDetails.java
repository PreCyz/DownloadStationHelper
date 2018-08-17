package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**Created by Gawa on 25/08/17.*/
@Getter
public class DSLoginDetails {

    @JsonProperty("is_portal_port")
    private boolean portalPort;
    private String sid;

}
