package pg.ui.task;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.*;
import pg.util.StringUtils;
import pg.web.model.ProgramMode;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Created by Gawa 2017-10-29 */
public class MainTask extends Task<Void> {

    private final ListView<ReducedDetail> listView;
    private String imdbId;

    private AppTask<List<TorrentDetail>> findTorrentsTask;
    private AppTask<List<ReducedDetail>> matchTorrents;
    private AppTask<Void> writeImdbMap;
    private AppTask startTorrents;
    private AppTask writeMatchTorrents;

    public MainTask(ListView<ReducedDetail> listView) {
        this.listView = listView;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    @Override
    protected Void call() throws Exception {
        updateProgress(0, 100);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        ProgramMode programMode;
        if (StringUtils.nullOrTrimEmpty(imdbId)) {
            programMode = ProgramMode.ALL_CONCURRENT;
            findTorrentsTask = new AppTask<>(new FindTorrentsCall(), executor);
        } else {
            programMode = ProgramMode.IMDB;
            findTorrentsTask = new AppTask<>(new FindTorrentsCall(imdbId), executor);
        }
        updateProgress(30, 100);
        updateMessage("Found torrents");

        writeImdbMap = new AppTask<>(new UpdateImdbMapCall(findTorrentsTask.get()), executor);
        updateProgress(35, 100);
        updateMessage("Imdb map stored");

        matchTorrents = new AppTask<>(new MatchTorrentsCall(programMode, findTorrentsTask.get()), executor);
        updateProgress(60, 100);
        updateMessage(messageAfterMatch());

        if (!matchTorrents.get().isEmpty()) {
            writeMatchTorrents = new AppTask<>(new WriteMatchTorrentsCall(matchTorrents.get()), executor);
            updateProgress(65, 100);
            updateMessage("Match torrents stored");
            startTorrents = new AppTask<>(new StartTorrentsCall(matchTorrents.get()), executor);
            updateMessage("Torrents started");
        } else {
            updateMessage("No torrents to start");
        }
        updateProgress(100, 100);

        return null;
    }

    private String messageAfterMatch() {
        if (matchTorrents.get().isEmpty()) {
            listView.setItems(
                    FXCollections.observableList(Collections.singletonList(ReducedDetail.NOTHING_TO_DISPLAY))
            );
        } else {
            listView.setItems(FXCollections.observableList(matchTorrents.get()));
        }
        String message = "No torrents to start.";
        if (matchTorrents.get().size() == 1) {
            message = "There is 1 torrents to start.";
        } else if (matchTorrents.get().size() > 1) {
            message = String.format("There are %s torrents to start.", matchTorrents.get().size());
        }
        return message;
    }

    public boolean isFindTorrentsTaskDone() {
        return findTorrentsTask != null && findTorrentsTask.isDone();
    }

    public boolean isMatchTorrentsDone() {
        return matchTorrents != null && matchTorrents.isDone();
    }

    public boolean isWriteImdbMapDone() {
        return writeImdbMap != null && writeImdbMap.isDone();
    }

    public boolean isStartTorrentsDone() {
        return startTorrents != null && startTorrents.isDone();
    }

    public boolean isWriteMatchTorrentsDone() {
        return writeMatchTorrents != null && writeMatchTorrents.isDone();
    }
}
