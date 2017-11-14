package pg.web.response;

import java.util.List;

/** Created by Gawa 2017-11-12 */
public class DeleteResponse {
    private List<DeleteItem> data;
    private boolean success;

    public List<DeleteItem> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}
