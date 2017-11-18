package pg.ui.task;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

    private final ExecutorService executor;
    private final String sid;
    private final ApiDetails downloadStationTask;
    private final List<DSTask> torrentsToDelete;
    private ObservableList<DSTask> dsTasks;

    public DeleteTask(String sid, ApiDetails downloadStationTask, List<DSTask> torrentsToDelete,
                      ExecutorService executor) {
        this.torrentsToDelete = torrentsToDelete;
        this.executor = executor;
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    protected Void call() throws Exception {
        new AppTask<>(new DeleteDSTaskCall(sid, torrentsToDelete, downloadStationTask), executor);

        AppTask<TaskListDetail> listOfTasks = new AppTask<>(new ListOfDSTaskCall(sid, downloadStationTask), executor);
        dsTasks = FXCollections.observableList(listOfTasks.get().getTasks());

        return null;
    }

    public ObservableValue<ObservableList<DSTask>> dsTaskList() {
        return new SimpleListProperty<>(dsTasks);
    }
}
