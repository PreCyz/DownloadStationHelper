package pg.ui.task;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.ds.DeleteDSTaskCall;
import pg.ui.task.atomic.call.ds.ListOfDSTaskCall;
import pg.web.model.ApiDetails;
import pg.web.response.DeleteItem;
import pg.web.response.detail.DSTask;
import pg.web.response.detail.TaskListDetail;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/** Created by Gawa 2017-10-29 */
public class DeleteTask extends Task<Void> {

    private final ExecutorService executor;
    private final String sid;
    private final ApiDetails downloadStationTask;
    private final List<DSTask> torrentsToDelete;
    private final Logger logger;
    private final ListView<DSTask> listView;

    public DeleteTask(ListView<DSTask> listView, String sid, ApiDetails downloadStationTask,
                      List<DSTask> torrentsToDelete, ExecutorService executor) {
        this.torrentsToDelete = torrentsToDelete;
        this.executor = executor;
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
        this.listView = listView;
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    protected Void call() throws Exception {
        updateProgress(0, 5);

        AppTask<List<DeleteItem>> deleteTask = deleteTasks();

        ObservableList<DSTask> dsTasks = getListOfTasks();
        updateListView(dsTasks);

        Set<String> deletedTasks = deleteTask.get().stream()
                .filter(deleteItem -> deleteItem.getError() == 0)
                .map(DeleteItem::getId)
                .collect(Collectors.toSet());
        updateProgress(4, 5);

        String logMsg = String.format("Tasks with ids [%s] deleted.", String.join(",", deletedTasks));
        updateMessage(logMsg);
        logger.info(logMsg);

        updateProgress(5, 5);

        return null;
    }

    private AppTask<List<DeleteItem>> deleteTasks() throws InterruptedException {
        AppTask<List<DeleteItem>> deleteTask = new AppTask<>(
                new DeleteDSTaskCall(sid, torrentsToDelete, downloadStationTask),
                executor
        );
        updateProgress(1, 5);

        while (!deleteTask.isDone()) {
            Thread.sleep(100);
        }
        updateProgress(2, 5);
        return deleteTask;
    }

    private ObservableList<DSTask> getListOfTasks() {
        AppTask<TaskListDetail> listOfTasks = new AppTask<>(new ListOfDSTaskCall(sid, downloadStationTask), executor);
        ObservableList<DSTask> dsTasks = FXCollections.observableList(listOfTasks.get().getTasks());
        if (dsTasks.isEmpty()) {
            dsTasks.add(DSTask.NOTHING_TO_DISPLAY);
        }
        return dsTasks;
    }

    private void updateListView(ObservableList<DSTask> dsTasks) {
        if (Platform.isFxApplicationThread()) {
            listView.setItems(dsTasks);
        } else {
            Platform.runLater(() -> listView.setItems(dsTasks));
        }
        updateProgress(3, 5);
    }
}
