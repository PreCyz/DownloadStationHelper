package pg.web.response.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import pg.web.model.ApiDetails;
import pg.web.model.ApiName;

/**Created by Gawa on 24/08/17.*/
public class SynologyDetailResponse {

    @JsonProperty(ApiName.API_AUTH)
    private ApiDetails authInfo;
    @JsonProperty(ApiName.DOWNLOAD_STATION_TASK)
    private ApiDetails downloadStationTask;

    public ApiDetails getAuthInfo() {
        return authInfo;
    }

    public ApiDetails getDownloadStationTask() {
        return downloadStationTask;
    }
}
