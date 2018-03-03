package pg.ui.window.controller.task;

import javafx.scene.control.TableView;
import pg.program.TaskDetail;
import pg.ui.window.controller.task.atomic.AppTask;
import pg.ui.window.controller.task.atomic.call.ds.DeleteForceCompleteCall;
import pg.web.ds.DSDeletedItem;
import pg.web.ds.detail.DSApiDetails;

import java.util.List;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class DeleteForceCompleteTask extends DeleteTask {

    public DeleteForceCompleteTask(TableView<TaskDetail> tableView, String sid, DSApiDetails downloadStationTask, List<TaskDetail> torrentsToDelete, ExecutorService executor) {
        super(tableView, sid, downloadStationTask, torrentsToDelete, executor);
    }

    @Override
    protected Void call() throws Exception {
        return super.call();
    }

    @Override
    protected AppTask<List<DSDeletedItem>> executeDSTasks() throws InterruptedException {
        AppTask<List<DSDeletedItem>> deleteTask = new AppTask<>(
                new DeleteForceCompleteCall(sid, torrentsToDelete, downloadStationTask),
                executor
        );
        updateProgress(2, 5);

        while (!deleteTask.isDone()) {
            Thread.sleep(100);
        }
        updateProgress(5, 5);
        return deleteTask;
    }
}
