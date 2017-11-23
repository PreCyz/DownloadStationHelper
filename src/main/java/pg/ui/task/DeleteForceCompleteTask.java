package pg.ui.task;

import javafx.scene.control.ListView;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.ds.DeleteForceCompleteCall;
import pg.web.model.ApiDetails;
import pg.web.response.DeleteItem;
import pg.web.response.detail.DSTask;

import java.util.List;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class DeleteForceCompleteTask extends DeleteTask {

    public DeleteForceCompleteTask(ListView<DSTask> listView, String sid, ApiDetails downloadStationTask, List<DSTask> torrentsToDelete, ExecutorService executor) {
        super(listView, sid, downloadStationTask, torrentsToDelete, executor);
    }

    @Override
    protected Void call() throws Exception {
        return super.call();
    }

    @Override
    protected AppTask<List<DeleteItem>> executeDSTasks() throws InterruptedException {
        AppTask<List<DeleteItem>> deleteTask = new AppTask<>(
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
