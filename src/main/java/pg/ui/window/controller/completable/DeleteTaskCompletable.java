package pg.ui.window.controller.completable;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.ui.window.controller.task.atomic.call.ds.DeleteCall;
import pg.ui.window.controller.task.atomic.call.ds.ListOfTaskCall;
import pg.web.ds.DSDeletedItem;
import pg.web.ds.detail.DSApiDetails;
import pg.web.ds.detail.DSTask;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/** Created by Gawa 2017-12-31 */
public class DeleteTaskCompletable extends Task<Void> {

    protected final ExecutorService executor;
    protected final String sid;
    protected final DSApiDetails downloadStationTask;
    protected final List<DSTask> torrentsToDelete;
    protected final Logger logger;
    private final ListView<DSTask> listView;

    public DeleteTaskCompletable(ListView<DSTask> listView, String sid, DSApiDetails downloadStationTask,
                                 List<DSTask> torrentsToDelete, ExecutorService executor) {
        this.torrentsToDelete = torrentsToDelete;
        this.executor = executor;
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
        this.listView = listView;
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    protected Void call() {
        CompletableFuture.supplyAsync(this::deleteDSTasks, executor)
                .thenApply(this::updateUIMessage)
                .thenApply(deletedItems -> getListOfDSTasks())
                .thenApply(this::updateDSTasksInUI);
        return null;
    }

    protected List<DSDeletedItem> deleteDSTasks() {
        updateProgress(1, 5);
        DeleteCall deleteCall = new DeleteCall(sid, torrentsToDelete, downloadStationTask);
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

    private ObservableList<DSTask> getListOfDSTasks() {
        ListOfTaskCall listOfTaskCall = new ListOfTaskCall(sid, downloadStationTask);
        ObservableList<DSTask> dsTasks = FXCollections.observableList(listOfTaskCall.call().getTasks());
        if (dsTasks.isEmpty()) {
            dsTasks.add(DSTask.NOTHING_TO_DISPLAY);
        }
        updateProgress(4, 5);
        return dsTasks;
    }

    private Void updateDSTasksInUI(ObservableList<DSTask> dsTasks) {
        if (Platform.isFxApplicationThread()) {
            listView.setItems(dsTasks);
            listView.requestFocus();
        } else {
            Platform.runLater(() -> {
                listView.setItems(dsTasks);
                listView.requestFocus();
            });
        }
        updateProgress(5, 5);
        return null;
    }
}
