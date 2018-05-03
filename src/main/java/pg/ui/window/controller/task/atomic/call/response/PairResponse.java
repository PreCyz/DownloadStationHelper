package pg.ui.window.controller.task.atomic.call.response;

public class PairResponse {
    private final String url;
    private final String response;

    public PairResponse(String url, String response) {
        this.url = url;
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "{url='" + url + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
