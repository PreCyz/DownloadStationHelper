package pg.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Created by Gawa 2017-11-12 */
public class DSDeleteResponse extends DSGeneralResponse {

    @JsonProperty("data")
    private List<DSDeletedItem> deletedItems;

    public List<DSDeletedItem> getDeletedItems() {
        return deletedItems;
    }
}
