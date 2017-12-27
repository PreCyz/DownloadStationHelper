package pg.ui.window.controller.task.atomic.call.ds;

import pg.web.client.GetClient;
import pg.web.model.SettingKeys;
import pg.web.model.torrent.ReducedDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-11-11 */
public class WriteTorrentsCall extends BasicCall implements Callable<Void> {

    private final List<ReducedDetail> matchTorrents;

    public WriteTorrentsCall(List<ReducedDetail> matchTorrents) {
        super();
        this.matchTorrents = matchTorrents;
    }

    @Override
    public Void call() {
        writeTorrentsOnDS();
        return null;
    }

    private void writeTorrentsOnDS() {
        String destination = application.getTorrentLocation("");
        if (destination == null || destination.isEmpty()) {
            logger.info("{} not specified. Add it to application.properties.", SettingKeys.TORRENT_LOCATION.key());
            return;
        }
        matchTorrents.forEach(torrent -> {
            String filePath = destination + torrent.getTitle() + ".torrent";
            new GetClient(torrent.getTorrentUrl()).downloadFile(filePath);
            logger.info("File '{}' saved in '{}'.", torrent.getTitle(), destination);
        });
    }
}
