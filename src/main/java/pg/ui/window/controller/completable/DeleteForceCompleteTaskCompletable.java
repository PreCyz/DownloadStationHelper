package pg.ui.window.controller.completable;

import javafx.scene.control.TableView;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.TaskDetail;
import pg.ui.window.controller.task.atomic.call.ds.DeleteForceCompleteCall;
import pg.web.ds.DSDeletedItem;
import pg.web.ds.detail.DSApiDetails;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class DeleteForceCompleteTaskCompletable extends DeleteTaskCompletable {

    public DeleteForceCompleteTaskCompletable(TableView<TaskDetail> tableView, String sid,
                                              DSApiDetails downloadStationTask, List<TaskDetail> torrentsToDelete,
                                              ExecutorService executor) {
        super(tableView, sid, downloadStationTask, torrentsToDelete, executor);
    }

    @Override
    protected Void call() {
        return super.call();
    }

    @Override
    protected List<DSDeletedItem> deleteDSTasks() {
        updateProgress(2, 5);
        try {
            List<DSDeletedItem> dsDeletedItems = CompletableFuture.supplyAsync(() -> {
                        DeleteForceCompleteCall deleteForceCompleteCall =
                                new DeleteForceCompleteCall(sid, torrentsToDelete, downloadStationTask);
                        return deleteForceCompleteCall.call();
                    },
                    executor).get();
            updateProgress(5, 5);
            return dsDeletedItems;
        } catch (InterruptedException | ExecutionException e) {
            throw new ProgramException(UIError.DELETE_TASK, e);
        }
    }
}
