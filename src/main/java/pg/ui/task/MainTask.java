package pg.ui.task;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
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

    private final ProgramMode programMode;
    private final ListView<ReducedDetail> listView;
    private final Text infoText;
    private String imdbId;

    private AppTask<List<TorrentDetail>> findTorrentsTask;
    private AppTask<List<ReducedDetail>> matchTorrents;
    private AppTask<Void> writeImdbMap;
    private AppTask startMatchingTorrents;
    private AppTask writeMatchTorrents;

    public MainTask(ListView<ReducedDetail> listView, Text infoText) {
        this.listView = listView;
        this.infoText = infoText;
        this.programMode = ProgramMode.ALL_CONCURRENT;
    }

    public MainTask(String imdbId, ListView<ReducedDetail> listView, Text infoText) {
        this.listView = listView;
        this.infoText = infoText;
        this.programMode = ProgramMode.IMDB;
        this.imdbId = imdbId;
    }

    @Override
    protected Void call() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        if (StringUtils.nullOrTrimEmpty(imdbId)) {
            findTorrentsTask = new AppTask<>(new FindTorrentsCall(), executor);
        } else {
            findTorrentsTask = new AppTask<>(new FindTorrentsCall(imdbId), executor);
        }

        matchTorrents = new AppTask<>(new MatchTorrentsCall(programMode, findTorrentsTask.get()), executor);
        updateView();
        writeImdbMap = new AppTask<>(new UpdateImdbMapCall(findTorrentsTask.get()), executor);

        if (!matchTorrents.get().isEmpty()) {
            startMatchingTorrents = new AppTask<>(new StartTorrentsCall(matchTorrents.get()), executor);
            writeMatchTorrents = new AppTask<>(new WriteMatchTorrentsCall(matchTorrents.get()), executor);
        }

        if (startMatchingTorrents.isDone()) {
            infoText.setText(String.format("Number of started torrents: %d.", matchTorrents.get().size()));
        }

        return null;
    }

    private void updateView() {
        Platform.runLater(() -> {
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
            infoText.setText(message);
        });
    }

    public boolean isFindTorrentsTaskDone() {
        return findTorrentsTask.isDone();
    }

    public boolean isMatchTorrentsDone() {
        return matchTorrents.isDone();
    }

    public boolean isWriteImdbMapDone() {
        return writeImdbMap.isDone();
    }

    public boolean isStartMatchingTorrentsDone() {
        return startMatchingTorrents.isDone();
    }

    public boolean isWriteMatchTorrentsDone() {
        return writeMatchTorrents.isDone();
    }
}
