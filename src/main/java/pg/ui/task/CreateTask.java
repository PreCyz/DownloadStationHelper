package pg.ui.task;

import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.StartTorrentsCall;
import pg.ui.task.atomic.call.WriteMatchTorrentsCall;
import pg.web.model.torrent.ReducedDetail;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Created by Gawa 2017-10-29 */
public class CreateTask extends Task<Void> {

    private final ListView<ReducedDetail> listView;
    private List<ReducedDetail> matchTorrents;

    private AppTask startTorrents;
    private AppTask writeMatchTorrents;

    public CreateTask(ListView<ReducedDetail> listView, List<ReducedDetail> matchTorrents) {
        this.listView = listView;
        this.matchTorrents = matchTorrents;
    }

    @Override
    protected Void call() throws Exception {
        updateProgress(0, 100);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        writeMatchTorrents = new AppTask<>(new WriteMatchTorrentsCall(matchTorrents), executor);
        updateProgress(65, 100);
        updateMessage("Match torrents stored");
        startTorrents = new AppTask<>(new StartTorrentsCall(matchTorrents), executor);
        updateMessage("Torrents started");
        updateProgress(100, 100);

        return null;
    }

    public boolean isStartTorrentsDone() {
        return startTorrents != null && startTorrents.isDone();
    }

    public boolean isWriteMatchTorrentsDone() {
        return writeMatchTorrents != null && writeMatchTorrents.isDone();
    }
}
