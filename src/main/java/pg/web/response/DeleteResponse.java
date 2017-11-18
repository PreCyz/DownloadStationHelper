package pg.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Created by Gawa 2017-11-12 */
public class DeleteResponse extends GeneralResponse {

    @JsonProperty("data")
    private List<DeleteItem> deletedItems;

    public List<DeleteItem> getDeletedItems() {
        return deletedItems;
    }
}
