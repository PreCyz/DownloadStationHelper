package pg.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import pg.web.response.detail.DSLoginDetails;

/**Created by Gawa on 24/08/17.*/
public class DSLoginResponse extends DSGeneralResponse {

    @JsonProperty("data")
    private DSLoginDetails DSLoginDetails;

    public DSLoginDetails getDSLoginDetails() {
        return DSLoginDetails;
    }
}
