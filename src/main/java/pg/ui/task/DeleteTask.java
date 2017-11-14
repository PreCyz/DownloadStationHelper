package pg.ui.task;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.ds.DeleteDSTaskCall;
import pg.ui.task.atomic.call.ds.ListOfDSTaskCall;
import pg.web.model.ApiDetails;
import pg.web.response.detail.DSTask;
import pg.web.response.detail.TaskListDetail;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Gawa 2017-10-29
 */
public class DeleteTask extends Task<Void> {

    private final ListView<DSTask> listView;
    private final ExecutorService executor;
    private final String sid;
    private final ApiDetails downloadStationTask;
    private final List<DSTask> torrentsToDelete;

    public DeleteTask(ListView<DSTask> listView, String sid, ApiDetails downloadStationTask,
                      List<DSTask> torrentsToDelete, ExecutorService executor) {
        this.listView = listView;
        this.torrentsToDelete = torrentsToDelete;
        this.executor = executor;
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    protected Void call() throws Exception {
        new AppTask<>(new DeleteDSTaskCall(sid, torrentsToDelete, downloadStationTask), executor);

        AppTask<TaskListDetail> listOfTasks = new AppTask<>(new ListOfDSTaskCall(sid, downloadStationTask), executor);
        listView.setItems(FXCollections.observableList(listOfTasks.get().getTasks()));

        return null;
    }
}
