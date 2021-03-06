package pg.ui.window.controller.completable;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.exceptions.ProgramException;
import pg.exceptions.UIError;
import pg.program.ProgramMode;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactory;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactoryBean;
import pg.ui.window.controller.task.atomic.call.torrent.*;
import pg.util.StringUtils;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DsApiDetail;
import pg.web.torrent.ReducedDetail;
import pg.web.torrent.TorrentDetail;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class FindTaskCompletable extends ListTaskCompletable {

    private final Logger logger;
    private ProgramMode programMode;
    private String imdbId;
    private Label numberOfShowsLabel;

    private List<ReducedDetail> matchTorrents;

    public FindTaskCompletable(Property<ObservableList<TaskDetail>> itemProperty, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                               CheckBox liveTrackCheckbox, ExecutorService executor) {
        super(itemProperty, dsApiDetail, windowHandler, liveTrackCheckbox, executor);
        this.programMode = ProgramMode.ALL_CONCURRENT;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
        this.programMode = ProgramMode.IMDB;
    }

    public void setNumberOfShowsLabel(Label numberOfShowsLabel) {
        this.numberOfShowsLabel = numberOfShowsLabel;
    }

    protected Void call() {
        CompletableFuture.supplyAsync(this::findTorrents, executor)
                .thenApply(this::updateImdbMap)
                .thenApply(this::matchTorrents)
                .thenApply(this::writeMatchTorrents)
                .thenApply(this::createTasks)
                .thenAccept(this::updateUIView);
        return null;
    }

    private List<TorrentDetail> findTorrents() {
        try {
            List<TorrentDetail> torrentDetails;
            if (StringUtils.nullOrTrimEmpty(imdbId)) {
                torrentDetails = new FindFavouriteTorrentsCall(this).call();
            } else {
                torrentDetails = new FindImdbTorrentsCall(imdbId, this).call();
            }
            updateProgress(30, 100);
            updateMessage("Found torrents");
            return torrentDetails;
        } catch (Exception ex) {
            logger.error("Could not find torrents.", ex);
            throw new ProgramException(UIError.GET_TORRENTS, ex);
        }
    }

    private List<TorrentDetail> updateImdbMap(List<TorrentDetail> torrents) {
        try {
            Integer imdbMapSize = new UpdateImdbMapCall(torrents).call();
            if (numberOfShowsLabel != null) {
                if (Platform.isFxApplicationThread()) {
                    numberOfShowsLabel.setText(String.valueOf(imdbMapSize));
                } else {
                    Platform.runLater(() -> numberOfShowsLabel.setText(String.valueOf(imdbMapSize)));
                }
            }
            updateProgress(35, 100);
            updateMessage("Imdb map stored");
            return torrents;
        } catch (Exception ex) {
            logger.error("Could not update imdb", ex);
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
            logger.error("Could not match torrents.", ex);
            throw new ProgramException(UIError.GET_TORRENTS, ex);
        }
    }

    private List<ReducedDetail> writeMatchTorrents(List<ReducedDetail> torrents) {
        try {
            if (!torrents.isEmpty()) {
                new WriteMatchTorrentsCall(torrents).call();
            }
            updateProgress(65, 100);
            updateMessage("Match torrents stored");
            return torrents;
        } catch (Exception ex) {
            logger.error("Could not write match torrents.", ex);
            throw new ProgramException(UIError.GET_TORRENTS, ex);
        }
    }

    private List<ReducedDetail> createTasks(List<ReducedDetail> torrents) {
        try {
            if (!torrents.isEmpty()) {
                if (getLoginSid() == null) {
                    loginToDiskStation();
                }
                ManageTaskFactoryBean factoryBean = new ManageTaskFactoryBean(
                        getLoginSid(), dsApiDetail.getDownloadStationTask(), DSTaskMethod.CREATE, torrents
                );
                ManageTaskFactory.getManageTask(factoryBean).call();
            }
        } catch (Exception ex) {
            logger.error("Could not create task.", ex);
        } finally {
            updateMessage("Torrents started");
            updateProgress(99, 100);
        }
        return torrents;
    }

    private void updateUIView(List<ReducedDetail> torrents) {
        if (torrents.isEmpty()) {
            updateUIWithNothingToDisplay();
        } else {
            updateUIView(getDsTaskListDetail());
        }
    }

    private void updateUIWithNothingToDisplay() {
        updateMessage("No torrents to start");
        updateProgress(100, 100);
        if (Platform.isFxApplicationThread()) {
            itemProperty.setValue(FXCollections.observableList(Collections.singletonList(TaskDetail.getNothingToDisplay())));
            updateLiveTracking();
        } else {
            Platform.runLater(() -> {
                itemProperty.setValue(FXCollections.observableList(Collections.singletonList(TaskDetail.getNothingToDisplay())));
                updateLiveTracking();
            });
        }
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
