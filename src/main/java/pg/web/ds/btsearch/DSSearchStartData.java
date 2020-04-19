package pg.web.ds.btsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DSSearchStartData {
    @JsonProperty("taskid")
    private String taskId;
}
