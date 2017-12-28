package pg.ui.window.controller.task;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.AppTask;
import pg.ui.window.controller.task.atomic.call.ds.ListOfTaskCall;
import pg.ui.window.controller.task.atomic.call.ds.LoginCall;
import pg.web.response.detail.DSTask;
import pg.web.response.detail.DSTaskListDetail;
import pg.web.response.detail.DsApiDetail;

import java.util.List;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class ListTask extends Task<Void> {

    protected final ListView<DSTask> listView;
    protected final ExecutorService executor;
    protected final DsApiDetail dsApiDetail;
    protected final WindowHandler windowHandler;

    protected String sid;

    public ListTask(ListView<DSTask> listView, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                    ExecutorService executor) {
        this.listView = listView;
        this.dsApiDetail = dsApiDetail;
        this.executor = executor;
        this.windowHandler = windowHandler;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    protected Void call() throws Exception {
        updateProgress(0, 10);

        if (sid == null) {
            updateProgress(3, 10);
            loginToDiskStation();
        }

        updateProgress(7, 10);
        getListOfTasks();

        updateMessage("List of tasks on disk station.");
        updateProgress(10, 10);
        return null;
    }

    protected void loginToDiskStation() {
        AppTask<String> loginToDsTask = new AppTask<>(new LoginCall(dsApiDetail.getAuthInfo()), executor);
        updateProgress(6, 10);
        updateMessage("Login in progress.");
        windowHandler.logoutOnExit();
        windowHandler.setDsApiDetail(dsApiDetail);
        sid = loginToDsTask.get();
    }

    protected void getListOfTasks() {
        AppTask<DSTaskListDetail> listOfTasks = new AppTask<>(
                new ListOfTaskCall(sid, dsApiDetail.getDownloadStationTask()), executor
        );
        List<DSTask> tasks = listOfTasks.get().getTasks();
        if (tasks.isEmpty()) {
            tasks.add(DSTask.NOTHING_TO_DISPLAY);
        }
        if (Platform.isFxApplicationThread()) {
            listView.setItems(FXCollections.observableList(tasks));
            listView.requestFocus();
        } else {
            Platform.runLater(() -> {
                listView.setItems(FXCollections.observableList(tasks));
                listView.requestFocus();
            });
        }
    }

    public String getLoginSid() {
        return sid;
    }
}
