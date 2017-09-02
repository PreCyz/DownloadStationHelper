package pg.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import pg.web.response.detail.SynologyDetailResponse;

/**Created by Gawa on 24/08/17.*/
public class SynologyResponse extends GeneralResponse {

    @JsonProperty("data")
    private SynologyDetailResponse detailResponse;

    public SynologyDetailResponse getDetailResponse() {
        return detailResponse;
    }

}
