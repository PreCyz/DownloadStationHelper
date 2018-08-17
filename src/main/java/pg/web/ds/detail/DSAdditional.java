package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/** Created by Gawa 2018-03-03 */
@Getter
public class DSAdditional {

    private DSAdditionalDetail detail;
    @JsonProperty("file")
    private List<DSFileDetail> fileDetails;

}
