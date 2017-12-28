package pg.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import pg.web.response.detail.DSTaskListDetail;

/**Created by Gawa on 27/08/17.*/
public class DSTaskListResponse extends DSGeneralResponse {

    @JsonProperty("data")
    private DSTaskListDetail DSTaskListDetail;

    public DSTaskListDetail getDSTaskListDetail() {
        return DSTaskListDetail;
    }
}
