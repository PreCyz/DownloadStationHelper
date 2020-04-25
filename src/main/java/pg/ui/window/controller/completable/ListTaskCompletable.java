package pg.ui.window.controller.completable;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import pg.converters.AbstractConverter;
import pg.converters.DSTaskToTaskDetailConverter;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.ListOfTaskCall;
import pg.ui.window.controller.task.atomic.call.ds.LoginCall;
import pg.web.ds.detail.DSTask;
import pg.web.ds.detail.DSTaskListDetail;
import pg.web.ds.detail.DsApiDetail;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class ListTaskCompletable extends UpdatableTask<Void> {

    protected final Property<ObservableList<TaskDetail>> itemProperty;
    protected final ExecutorService executor;
    protected final DsApiDetail dsApiDetail;
    protected final WindowHandler windowHandler;
    private final CheckBox liveTrackCheckbox;

    private String sid;

    public ListTaskCompletable(Property<ObservableList<TaskDetail>> itemProperty, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                               CheckBox liveTrackCheckbox, ExecutorService executor) {
        this.itemProperty = itemProperty;
        this.dsApiDetail = dsApiDetail;
        this.executor = executor;
        this.windowHandler = windowHandler;
        this.liveTrackCheckbox = liveTrackCheckbox;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    protected Void call() {
        updateProgress(0, 100);

        if (sid == null) {
            CompletableFuture.supplyAsync(this::loginToDiskStation, executor)
                    .thenApply(resultSid -> getDsTaskListDetail())
                    .thenAccept(this::updateUIView);
        } else {
            CompletableFuture.supplyAsync(this::getDsTaskListDetail, executor)
                    .thenAccept(this::updateUIView);
        }
        return null;
    }

    protected String loginToDiskStation() {
        updateProgress(30, 100);
        LoginCall loginCall = new LoginCall(dsApiDetail.getAuthInfo());
        updateProgress(60, 100);
        updateMessage("Login in progress.");
        windowHandler.logoutOnExit();
        windowHandler.setDsApiDetail(dsApiDetail);
        setSid(loginCall.call());
        return getLoginSid();
    }

    protected DSTaskListDetail getDsTaskListDetail() {
        updateProgress(70, 100);

        ListOfTaskCall listOfTaskCall = new ListOfTaskCall(sid, dsApiDetail.getDownloadStationTask());
        return listOfTaskCall.call();
    }

    protected void updateUIView(DSTaskListDetail listOfTasks) {
        AbstractConverter<DSTask, TaskDetail> converter = new DSTaskToTaskDetailConverter();
        List<TaskDetail> tasks = converter.convert(listOfTasks.getTasks());
        if (tasks.isEmpty()) {
            tasks.add(TaskDetail.getNothingToDisplay());
        }
        if (Platform.isFxApplicationThread()) {
            itemProperty.setValue(FXCollections.observableList(tasks));
            updateLiveTracking();
        } else {
            Platform.runLater(() -> {
                itemProperty.setValue(FXCollections.observableList(tasks));
                updateLiveTracking();
            });
        }

        updateMessage("List of tasks on disk station.");
        updateProgress(100, 100);
    }

    public String getLoginSid() {
        return sid;
    }

    @Override
    public void updateProgressTo30(double workDone) {
        updateProgress(workDone * 30, 100);
    }

    protected void updateLiveTracking() {
        if (itemProperty.getValue().size() == 1 && itemProperty.getValue().get(0).isNothingToDisplay()) {
            liveTrackCheckbox.setSelected(false);
            liveTrackCheckbox.setDisable(true);
        } else {
            liveTrackCheckbox.setDisable(false);
        }
    }
}
