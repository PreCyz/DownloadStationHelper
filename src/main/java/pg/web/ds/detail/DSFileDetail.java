package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/** Created by Gawa 2018-03-03 */
@Getter
public class DSFileDetail {

    private String filename;
    private long index;
    private String priority;
    private String size;
    @JsonProperty("size_downloaded")
    private String sizeDownloaded;
    private boolean wanted;

}
