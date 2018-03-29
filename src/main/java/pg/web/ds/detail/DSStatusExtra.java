package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DSStatusExtra {
    @JsonProperty("error_detail")
    private String errorDetail;

    public String getErrorDetail() {
        return errorDetail;
    }
}
