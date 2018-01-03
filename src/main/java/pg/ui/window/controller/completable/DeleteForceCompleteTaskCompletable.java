package pg.ui.window.controller.completable;

import javafx.scene.control.ListView;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.ui.window.controller.task.atomic.call.ds.DeleteForceCompleteCall;
import pg.web.ds.DSDeletedItem;
import pg.web.ds.detail.DSApiDetails;
import pg.web.ds.detail.DSTask;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class DeleteForceCompleteTaskCompletable extends DeleteTaskCompletable {

    public DeleteForceCompleteTaskCompletable(ListView<DSTask> listView, String sid, DSApiDetails downloadStationTask, List<DSTask> torrentsToDelete, ExecutorService executor) {
        super(listView, sid, downloadStationTask, torrentsToDelete, executor);
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
