package pg.ui.task.atomic.call;

import pg.web.client.GetClient;

import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-25 */
public class GetTorrentsCall implements Callable<String> {

    private final String url;

    public GetTorrentsCall(String url) {
        this.url = url;
    }

    @Override
    public String call() throws Exception {
        GetClient client = new GetClient(url);
        if (client.get().isPresent()) {
            return client.get().get();
        }
        return "";
    }

    public String getUrl() {
        return url;
    }
}
