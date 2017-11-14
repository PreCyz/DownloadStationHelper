package pg.ui.task;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.FindTorrentsCall;
import pg.ui.task.atomic.call.MatchTorrentsCall;
import pg.ui.task.atomic.call.UpdateImdbMapCall;
import pg.ui.task.atomic.call.WriteMatchTorrentsCall;
import pg.ui.task.atomic.call.ds.CreateDSTaskCall;
import pg.ui.task.atomic.call.ds.ListOfDSTaskCall;
import pg.util.StringUtils;
import pg.web.model.ApiDetails;
import pg.web.model.ProgramMode;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;
import pg.web.response.detail.DSTask;
import pg.web.response.detail.TaskListDetail;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class FindTask extends Task<Void> {

    private final ListView<DSTask> listView;
    private final ExecutorService executor;
    private final String sid;
    private final ApiDetails downloadStationTask;
    private String imdbId;

    private AppTask<List<ReducedDetail>> matchTorrents;
    private AppTask<TaskListDetail> listOfTasks;

    public FindTask(ListView<DSTask> listView, String sid, ApiDetails downloadStationTask,
                    ExecutorService executor) {
        this.listView = listView;
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
        this.executor = executor;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    @Override
    protected Void call() throws Exception {
        updateProgress(0, 100);
        ProgramMode programMode;
        AppTask<List<TorrentDetail>> findTorrentsTask;
        if (StringUtils.nullOrTrimEmpty(imdbId)) {
            programMode = ProgramMode.ALL_CONCURRENT;
            findTorrentsTask = new AppTask<>(new FindTorrentsCall(), executor);
        } else {
            programMode = ProgramMode.IMDB;
            findTorrentsTask = new AppTask<>(new FindTorrentsCall(imdbId), executor);
        }
        updateProgress(30, 100);
        updateMessage("Found torrents");

        new AppTask<>(new UpdateImdbMapCall(findTorrentsTask.get()), executor);
        updateProgress(35, 100);
        updateMessage("Imdb map stored");

        matchTorrents = new AppTask<>(new MatchTorrentsCall(programMode, findTorrentsTask.get()), executor);
        updateProgress(60, 100);
        updateMessage(messageAfterMatch());

        if (matchTorrents.get().isEmpty()) {
            updateMessage("No torrents to start");
            updateProgress(100, 100);
            listView.setItems(
                    FXCollections.observableList(Collections.singletonList(DSTask.NOTHING_TO_DISPLAY))
            );
            return null;
        }

        new AppTask<>(new WriteMatchTorrentsCall(matchTorrents.get()), executor);
        updateProgress(65, 100);
        updateMessage("Match torrents stored");
        AppTask<Void> createTasks = new AppTask<>(
                new CreateDSTaskCall(sid, matchTorrents.get(), downloadStationTask), executor);
        updateMessage("Torrents started");
        updateProgress(99, 99);

        while (!createTasks.isDone()) {
            Thread.sleep(100);
        }

        listOfTasks = new AppTask<>(new ListOfDSTaskCall(sid, downloadStationTask), executor);
        listView.setItems(FXCollections.observableList(listOfTasks.get().getTasks()));

        updateMessage("Torrents started. Pick torrents and press DEL to delete.");
        updateProgress(100, 100);

        return null;
    }

    private String messageAfterMatch() {
        String message = "No torrents to start.";
        if (matchTorrents.get().size() == 1) {
            message = "There is 1 torrents to start.";
        } else if (matchTorrents.get().size() > 1) {
            message = String.format("There are %s torrents to start.", matchTorrents.get().size());
        }
        return message;
    }

    public List<ReducedDetail> getMatchTorrents() {
        return matchTorrents.get();
    }

    public TaskListDetail getTaskListDetail() {
        return listOfTasks.get();
    }

    public boolean isMatchTorrentsDone() {
        return matchTorrents != null && matchTorrents.isDone();
    }
}
