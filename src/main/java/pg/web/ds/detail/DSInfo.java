package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pg.program.ApiName;

/**Created by Gawa on 24/08/17.*/
@Getter
public class DSInfo {

    @JsonProperty(ApiName.API_AUTH)
    private DSApiDetails authInfo;
    @JsonProperty(ApiName.DOWNLOAD_STATION_TASK)
    private DSApiDetails downloadStationTask;

}
