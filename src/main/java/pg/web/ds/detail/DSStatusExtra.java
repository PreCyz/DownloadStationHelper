package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DSStatusExtra {

    @JsonProperty("error_detail")
    private String errorDetail;

}
