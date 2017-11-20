package pg.ui.task;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import pg.ui.handler.WindowHandler;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.FindTorrentsCall;
import pg.ui.task.atomic.call.MatchTorrentsCall;
import pg.ui.task.atomic.call.UpdateImdbMapCall;
import pg.ui.task.atomic.call.WriteMatchTorrentsCall;
import pg.ui.task.atomic.call.ds.CreateDSTaskCall;
import pg.ui.task.atomic.call.ds.DsApiDetail;
import pg.ui.task.atomic.call.ds.ListOfDSTaskCall;
import pg.ui.task.atomic.call.ds.LoginDSCall;
import pg.util.StringUtils;
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
    private final DsApiDetail dsApiDetail;
    private final WindowHandler windowHandler;
    private ProgramMode programMode;
    private String imdbId;

    private AppTask<List<ReducedDetail>> matchTorrents;
    private AppTask<String> loginToDsTask;
    private String sid;

    public FindTask(ListView<DSTask> listView, DsApiDetail dsApiDetail, WindowHandler windowHandler, ExecutorService executor) {
        this.listView = listView;
        this.dsApiDetail = dsApiDetail;
        this.executor = executor;
        this.windowHandler = windowHandler;
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
                listView.setItems(FXCollections.observableList(Collections.singletonList(DSTask.NOTHING_TO_DISPLAY)));
            } else {
                Platform.runLater(() ->
                        listView.setItems(FXCollections.observableList(Collections.singletonList(DSTask.NOTHING_TO_DISPLAY)))
                );
            }
            return null;
        }

        writeMatchTorrents();
        loginToDiskStation();
        createTasks();
        getListOfTasks();

        updateMessage("Torrents started. Pick torrents and press DEL to delete.");
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

    private void loginToDiskStation() {
        loginToDsTask = new AppTask<>(new LoginDSCall(dsApiDetail.getAuthInfo()), executor);
        updateProgress(70, 100);
        updateMessage("Login to DS done.");
        windowHandler.logoutOnExit();
        windowHandler.setDsApiDetail(dsApiDetail);
    }

    private void createTasks() throws InterruptedException {
        sid = loginToDsTask.get();
        AppTask<Void> createTasks = new AppTask<>(
                new CreateDSTaskCall(sid, matchTorrents.get(), dsApiDetail.getDownloadStationTask()),
                executor
        );
        updateMessage("Torrents started");
        updateProgress(99, 99);

        while (!createTasks.isDone()) {
            Thread.sleep(100);
        }
    }

    private void getListOfTasks() {
        AppTask<TaskListDetail> listOfTasks = new AppTask<>(
                new ListOfDSTaskCall(loginToDsTask.get(), dsApiDetail.getDownloadStationTask()),
                executor
        );
        if (Platform.isFxApplicationThread()) {
            listView.setItems(FXCollections.observableList(listOfTasks.get().getTasks()));
        } else {
            Platform.runLater(() -> listView.setItems(FXCollections.observableList(listOfTasks.get().getTasks())));
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

    public String getLoginSid() {
        return sid;
    }
}
