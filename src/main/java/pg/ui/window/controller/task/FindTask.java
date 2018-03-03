package pg.ui.window.controller.task;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import pg.program.ProgramMode;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.AppTask;
import pg.ui.window.controller.task.atomic.call.ds.CreateTaskCall;
import pg.ui.window.controller.task.atomic.call.torrent.FindTorrentsCall;
import pg.ui.window.controller.task.atomic.call.torrent.MatchTorrentsCall;
import pg.ui.window.controller.task.atomic.call.torrent.UpdateImdbMapCall;
import pg.ui.window.controller.task.atomic.call.torrent.WriteMatchTorrentsCall;
import pg.util.StringUtils;
import pg.web.ds.detail.DsApiDetail;
import pg.web.torrent.ReducedDetail;
import pg.web.torrent.TorrentDetail;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class FindTask extends ListTask {

    private ProgramMode programMode;
    private String imdbId;

    private AppTask<List<ReducedDetail>> matchTorrents;

    public FindTask(TableView<TaskDetail> tableView, DsApiDetail dsApiDetail, WindowHandler windowHandler, ExecutorService executor) {
        super(tableView, dsApiDetail, windowHandler, executor);
        this.programMode = ProgramMode.ALL_CONCURRENT;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
        this.programMode = ProgramMode.IMDB;
    }

    @Override
    protected Void call() throws Exception {
        updateProgress(0, 100);

        AppTask<List<TorrentDetail>> findTorrentsTask = findTorrents();
        updateImdbMap(findTorrentsTask);
        matchTorrents(findTorrentsTask);

        if (matchTorrents.get().isEmpty()) {
            updateMessage("No torrents to start");
            updateProgress(100, 100);
            if (Platform.isFxApplicationThread()) {
                tableView.setItems(FXCollections.observableList(Collections.singletonList(TaskDetail.getNothingToDisplay())));
                tableView.requestFocus();
            } else {
                Platform.runLater(() -> {
                    tableView.setItems(FXCollections.observableList(Collections.singletonList(TaskDetail.getNothingToDisplay())));
                    tableView.requestFocus();
                });
            }
            return null;
        }

        writeMatchTorrents();
        if (sid == null) {
            loginToDiskStation();
        }
        createTasks();
        getListOfTasks();

        updateMessage("Torrents started.");
        updateProgress(100, 100);
        return null;
    }

    private AppTask<List<TorrentDetail>> findTorrents() {
        AppTask<List<TorrentDetail>> findTorrentsTask;
        if (StringUtils.nullOrTrimEmpty(imdbId)) {
            findTorrentsTask = new AppTask<>(new FindTorrentsCall(), executor);
        } else {
            findTorrentsTask = new AppTask<>(new FindTorrentsCall(imdbId), executor);
        }
        updateProgress(30, 100);
        updateMessage("Found torrents");
        return findTorrentsTask;
    }

    private void updateImdbMap(AppTask<List<TorrentDetail>> findTorrentsTask) {
        new AppTask<>(new UpdateImdbMapCall(findTorrentsTask.get()), executor);
        updateProgress(35, 100);
        updateMessage("Imdb map stored");
    }

    private void matchTorrents(AppTask<List<TorrentDetail>> findTorrentsTask) {
        matchTorrents = new AppTask<>(new MatchTorrentsCall(programMode, findTorrentsTask.get()), executor);
        updateProgress(60, 100);
        updateMessage(messageAfterMatch());
    }

    private void writeMatchTorrents() {
        new AppTask<>(new WriteMatchTorrentsCall(matchTorrents.get()), executor);
        updateProgress(65, 100);
        updateMessage("Match torrents stored");
    }

    private void createTasks() throws InterruptedException {
        AppTask<Void> createTasks = new AppTask<>(
                new CreateTaskCall(sid, matchTorrents.get(), dsApiDetail.getDownloadStationTask()),
                executor
        );
        updateMessage("Torrents started");
        updateProgress(99, 99);

        while (!createTasks.isDone()) {
            Thread.sleep(100);
        }
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
}
