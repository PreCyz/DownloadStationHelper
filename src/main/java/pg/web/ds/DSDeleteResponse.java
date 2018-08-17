package pg.web.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/** Created by Gawa 2017-11-12 */
@Getter
public class DSDeleteResponse extends DSGeneralResponse {

    @JsonProperty("data")
    private List<DSDeletedItem> deletedItems;

}
