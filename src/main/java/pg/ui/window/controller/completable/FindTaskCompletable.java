package pg.ui.window.controller.completable;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.ProgramMode;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.CreateCall;
import pg.ui.window.controller.task.atomic.call.torrent.FindTorrentsCall;
import pg.ui.window.controller.task.atomic.call.torrent.MatchTorrentsCall;
import pg.ui.window.controller.task.atomic.call.torrent.UpdateImdbMapCall;
import pg.ui.window.controller.task.atomic.call.torrent.WriteMatchTorrentsCall;
import pg.util.StringUtils;
import pg.web.ds.detail.DSTask;
import pg.web.ds.detail.DsApiDetail;
import pg.web.torrent.ReducedDetail;
import pg.web.torrent.TorrentDetail;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class FindTaskCompletable extends ListTaskCompletable {

    private ProgramMode programMode;
    private String imdbId;

    private List<ReducedDetail> matchTorrents;

    public FindTaskCompletable(ListView<DSTask> listView, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                               ExecutorService executor) {
        super(listView, dsApiDetail, windowHandler, executor);
        this.programMode = ProgramMode.ALL_CONCURRENT;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
        this.programMode = ProgramMode.IMDB;
    }

    @Override
    protected Void call() {
        updateProgress(0, 100);

        CompletableFuture<List<ReducedDetail>> matchTorrents = CompletableFuture.supplyAsync(this::findTorrents, executor)
                .thenApply(this::updateImdbMap).thenApply(this::matchTorrents);

        if (this.matchTorrents.isEmpty()) {
            matchTorrents.thenAccept(torrents -> updateUIWithNothingToDisplay());
            return null;
        }

        matchTorrents.thenAccept(this::writeMatchTorrents);
        if (getLoginSid() == null) {
            matchTorrents.thenAccept(torrents -> loginToDiskStation());
        }
        matchTorrents.thenAccept(this::createTasks)
                .thenApply(a -> getDsTaskListDetail())
                .thenAccept(this::updateUIView);

        updateMessage("Torrents started.");
        updateProgress(100, 100);
        return null;
    }

    private List<TorrentDetail> findTorrents() {
        try {
            List<TorrentDetail> torrentDetails;
            if (StringUtils.nullOrTrimEmpty(imdbId)) {
                torrentDetails = new FindTorrentsCall().call();
            } else {
                torrentDetails = new FindTorrentsCall(imdbId).call();
            }
            updateProgress(30, 100);
            updateMessage("Found torrents");
            return torrentDetails;
        } catch (Exception ex) {
            throw new ProgramException(UIError.GET_TORRENTS, ex);
        }
    }

    private List<TorrentDetail> updateImdbMap(List<TorrentDetail> torrents) {
        try {
            new UpdateImdbMapCall(torrents).call();
            updateProgress(35, 100);
            updateMessage("Imdb map stored");
            return torrents;
        } catch (Exception ex) {
            throw new ProgramException(UIError.GET_TORRENTS, ex);
        }
    }

    private List<ReducedDetail> matchTorrents(List<TorrentDetail> torrents) {
        try {
            this.matchTorrents = new MatchTorrentsCall(programMode, torrents).call();
            updateProgress(60, 100);
            updateMessage(messageAfterMatch());
            return this.matchTorrents;
        } catch (Exception ex) {
            throw new ProgramException(UIError.GET_TORRENTS, ex);
        }
    }

    private void updateUIWithNothingToDisplay() {
        updateMessage("No torrents to start");
        updateProgress(100, 100);
        if (Platform.isFxApplicationThread()) {
            listView.setItems(FXCollections.observableList(Collections.singletonList(DSTask.NOTHING_TO_DISPLAY)));
            listView.requestFocus();
        } else {
            Platform.runLater(() -> {
                listView.setItems(FXCollections.observableList(Collections.singletonList(DSTask.NOTHING_TO_DISPLAY)));
                listView.requestFocus();
            });
        }
    }

    private List<ReducedDetail> writeMatchTorrents(List<ReducedDetail> torrents) {
        try {
            new WriteMatchTorrentsCall(torrents).call();
            updateProgress(65, 100);
            updateMessage("Match torrents stored");
            return torrents;
        } catch (Exception ex) {
            throw new ProgramException(UIError.GET_TORRENTS, ex);
        }
    }

    private void createTasks(List<ReducedDetail> torrents) {
        new CreateCall(getLoginSid(), torrents, dsApiDetail.getDownloadStationTask()).call();
        updateMessage("Torrents started");
        updateProgress(99, 99);
    }

    private String messageAfterMatch() {
        String message = "No torrents to start.";
        if (matchTorrents.size() == 1) {
            message = "There is 1 torrents to start.";
        } else if (matchTorrents.size() > 1) {
            message = String.format("There are %s torrents to start.", matchTorrents.size());
        }
        return message;
    }
}
