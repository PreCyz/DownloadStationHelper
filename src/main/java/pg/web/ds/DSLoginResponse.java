package pg.web.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pg.web.ds.detail.DSLoginDetails;

/**Created by Gawa on 24/08/17.*/
@Getter
public class DSLoginResponse extends DSGeneralResponse {

    @JsonProperty("data")
    private DSLoginDetails DSLoginDetails;
    private boolean success;
}
