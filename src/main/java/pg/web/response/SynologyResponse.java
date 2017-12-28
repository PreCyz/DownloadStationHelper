package pg.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import pg.web.response.detail.DSInfo;

/**Created by Gawa on 24/08/17.*/
public class SynologyResponse extends GeneralResponse {

    @JsonProperty("data")
    private DSInfo dsInfo;

    public DSInfo getDsInfo() {
        return dsInfo;
    }

}
