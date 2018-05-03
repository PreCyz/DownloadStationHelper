package pg.ui.window.controller.completable;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.DeleteCall;
import pg.web.ds.DSDeletedItem;
import pg.web.ds.detail.DsApiDetail;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/** Created by Gawa 2017-12-31 */
public class DeleteTaskCompletable extends ListTaskCompletable {

    protected final List<TaskDetail> torrentsToDelete;
    protected final Logger logger;

    public DeleteTaskCompletable(TableView<TaskDetail> tableView, DsApiDetail dsApiDetail,
                                 List<TaskDetail> torrentsToDelete, WindowHandler windowHandler,
                                 CheckBox liveTrackCheckbox, ExecutorService executor) {
        super(tableView, dsApiDetail, windowHandler, liveTrackCheckbox, executor);
        this.torrentsToDelete = torrentsToDelete;
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    protected Void call() {
        CompletableFuture.supplyAsync(this::deleteDSTasks, executor)
                .thenApply(this::updateUIMessage)
                .thenApply(items -> getDsTaskListDetail())
                .thenAccept(this::updateUIView);
        return null;
    }

    protected List<DSDeletedItem> deleteDSTasks() {
        updateProgress(1, 5);
        DeleteCall deleteCall = new DeleteCall(getLoginSid(), torrentsToDelete, dsApiDetail.getDownloadStationTask());
        updateProgress(2, 5);
        try {
            return deleteCall.call();
        } catch (Exception ex) {
            throw new ProgramException(UIError.DELETE_TASK, ex);
        }
    }

    private Set<String> updateUIMessage(List<DSDeletedItem> dsDeletedItems) {
        Set<String> deletedTasks = dsDeletedItems.stream()
                .filter(deleteItem -> deleteItem.getError() == 0)
                .map(DSDeletedItem::getId)
                .collect(Collectors.toSet());
        updateProgress(3, 5);

        String logMsg = String.format("Tasks with ids [%s] deleted.", String.join(",", deletedTasks));
        updateMessage(logMsg);
        logger.info(logMsg);
        return deletedTasks;
    }
}
