package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pg.web.ds.DSTaskDownloadStatus;

/**Created by Gawa on 27/08/17.*/
@NoArgsConstructor
@Getter
public class DSTask {
    private String id;
    private long size;
    private DSTaskDownloadStatus status;
    private String title;
    private String type;
    private String username;
    private DSAdditional additional;
    @JsonProperty("status_extra")
    private DSStatusExtra statusExtra;

    private DSTask(String title) {
        this.title = title;
    }

}
