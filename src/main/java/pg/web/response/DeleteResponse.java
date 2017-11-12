package pg.web.response;

/** Created by Gawa 2017-11-12 */
public class DeleteResponse {
    private int error;
    private String id;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
