package pg.web.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pg.web.ds.detail.DSTaskListDetail;

/**Created by Gawa on 27/08/17.*/
@Getter
public class DSTaskListResponse extends DSGeneralResponse {

    @JsonProperty("data")
    private DSTaskListDetail DSTaskListDetail;

}
