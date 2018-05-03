package pg.ui.window.controller.task.atomic.call.torrent;

import pg.ui.window.controller.task.atomic.call.response.PairResponse;
import pg.web.client.GetClient;

import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-25 */
public class GetTorrentsCustomCall implements Callable<PairResponse> {

    private final String url;

    public GetTorrentsCustomCall(String url) {
        this.url = url;
    }

    @Override
    public PairResponse call() {
        GetClient client = new GetClient(url);
        if (client.get().isPresent()) {
            return new PairResponse(url, client.get().get());
        }
        return new PairResponse("", "");
    }
}
