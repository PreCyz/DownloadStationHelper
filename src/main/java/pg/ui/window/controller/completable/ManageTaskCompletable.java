package pg.ui.window.controller.completable;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskCall;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactory;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactoryBean;
import pg.web.ds.DSItem;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DsApiDetail;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public abstract class ManageTaskCompletable extends ListTaskCompletable  {
    private  List<TaskDetail> torrentsToManage;
    private final Logger logger;

    ManageTaskCompletable(TableView<TaskDetail> tableView, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                          List<TaskDetail> torrentsToManage, CheckBox liveTrackCheckbox, ExecutorService executor) {
        super(tableView, dsApiDetail, windowHandler, liveTrackCheckbox, executor);
        this.torrentsToManage = torrentsToManage;
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    protected Void call() {
        CompletableFuture.supplyAsync(this::manageTasks, executor)
                .thenApply(this::updateUIMessage)
                .thenApply(items -> getDsTaskListDetail())
                .thenAccept(this::updateUIView);
        return null;
    }

    private List<DSItem> manageTasks() {
        updateProgress(2, 5);
        try {
            Callable<List<DSItem>> manageTask = getManageTask();
            return manageTask.call();
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

        String logMsg = String.format("Tasks with ids [%s] %s.", String.join(",", deletedTasks), getTaskMethod().method());
        updateMessage(logMsg);
        logger.info(logMsg);
        return deletedTasks;
    }

    private ManageTaskCall getManageTask() {
        ManageTaskFactoryBean bean = new ManageTaskFactoryBean(
                getLoginSid(), torrentsToManage, dsApiDetail.getDownloadStationTask(), getTaskMethod()
        );
        return ManageTaskFactory.getManageTask(bean);
    }

    protected abstract DSTaskMethod getTaskMethod();
}
