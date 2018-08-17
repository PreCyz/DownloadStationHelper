package pg.web.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pg.web.ds.detail.DSInfo;

/**Created by Gawa on 24/08/17.*/
@Getter
public class DSResponse extends DSGeneralResponse {

    @JsonProperty("data")
    private DSInfo dsInfo;

}
