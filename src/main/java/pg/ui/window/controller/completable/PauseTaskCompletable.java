package pg.ui.window.controller.completable;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.PauseCall;
import pg.web.ds.DSItem;
import pg.web.ds.detail.DsApiDetail;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class PauseTaskCompletable extends ListTaskCompletable {

    protected final List<TaskDetail> torrentsToPause;
    protected final Logger logger;

    public PauseTaskCompletable(TableView<TaskDetail> tableView, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                                List<TaskDetail> torrentsToPause, CheckBox liveTrackCheckbox, ExecutorService executor) {
        super(tableView, dsApiDetail, windowHandler, liveTrackCheckbox, executor);
        this.torrentsToPause = torrentsToPause;
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    protected Void call() {
        CompletableFuture.supplyAsync(this::pauseDSTasks, executor)
                .thenApply(this::updateUIMessage)
                .thenApply(items -> getDsTaskListDetail())
                .thenAccept(this::updateUIView);
        return null;
    }

    protected List<DSItem> pauseDSTasks() {
        updateProgress(1, 5);
        PauseCall pauseCall = new PauseCall(getLoginSid(), torrentsToPause, dsApiDetail.getDownloadStationTask());
        updateProgress(2, 5);
        try {
            return pauseCall.call();
        } catch (Exception ex) {
            throw new ProgramException(UIError.PAUSE_TASK, ex);
        }
    }

    private Set<String> updateUIMessage(List<DSItem> dsItems) {
        Set<String> deletedTasks = dsItems.stream()
                .filter(deleteItem -> deleteItem.getError() == 0)
                .map(DSItem::getId)
                .collect(Collectors.toSet());
        updateProgress(3, 5);

        String logMsg = String.format("Tasks with ids [%s] deleted.", String.join(",", deletedTasks));
        updateMessage(logMsg);
        logger.info(logMsg);
        return deletedTasks;
    }
}
