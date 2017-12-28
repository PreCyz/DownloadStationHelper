package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import pg.web.program.ApiName;

/**Created by Gawa on 24/08/17.*/
public class DSInfo {

    @JsonProperty(ApiName.API_AUTH)
    private DSApiDetails authInfo;
    @JsonProperty(ApiName.DOWNLOAD_STATION_TASK)
    private DSApiDetails downloadStationTask;

    public DSApiDetails getAuthInfo() {
        return authInfo;
    }

    public DSApiDetails getDownloadStationTask() {
        return downloadStationTask;
    }
}
