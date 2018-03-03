package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Created by Gawa 2018-03-03 */
public class DSAdditional {

    private DSAdditionalDetail detail;
    @JsonProperty("file")
    private List<DSFileDetail> fileDetails;

    public DSAdditionalDetail getDetail() {
        return detail;
    }

    public List<DSFileDetail> getFileDetails() {
        return fileDetails;
    }
}