package pg.ui.window.controller.completable;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.converter.Converter;
import pg.converter.DSTaskToTaskDetailConverter;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.TaskDetail;
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
    protected final List<TaskDetail> torrentsToDelete;
    protected final Logger logger;
    private final TableView<TaskDetail> tableView;

    public DeleteTaskCompletable(TableView<TaskDetail> tableView, String sid, DSApiDetails downloadStationTask,
                                 List<TaskDetail> torrentsToDelete, ExecutorService executor) {
        this.torrentsToDelete = torrentsToDelete;
        this.executor = executor;
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
        this.tableView = tableView;
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    protected Void call() {
        CompletableFuture.supplyAsync(this::deleteDSTasks, executor)
                .thenApply(this::updateUIMessage)
                .thenApply(deletedItems -> getTaskDetails())
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

    private ObservableList<TaskDetail> getTaskDetails() {
        ListOfTaskCall listOfTaskCall = new ListOfTaskCall(sid, downloadStationTask);
        Converter<DSTask, TaskDetail> converter = new DSTaskToTaskDetailConverter<>();
        ObservableList<TaskDetail> taskDetails = FXCollections.observableList(converter.convert(listOfTaskCall.call().getTasks()));
        if (taskDetails.isEmpty()) {
            taskDetails.add(TaskDetail.getNothingToDisplay());
        }
        updateProgress(4, 5);
        return taskDetails;
    }

    private Void updateDSTasksInUI(final ObservableList<TaskDetail> taskDetails) {
        if (Platform.isFxApplicationThread()) {
            tableView.setItems(taskDetails);
            tableView.requestFocus();
        } else {
            Platform.runLater(() -> {
                tableView.setItems(taskDetails);
                tableView.requestFocus();
            });
        }
        updateProgress(5, 5);
        return null;
    }
}
