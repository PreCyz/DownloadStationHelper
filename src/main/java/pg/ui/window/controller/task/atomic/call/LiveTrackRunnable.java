package pg.ui.window.controller.task.atomic.call;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.converters.AbstractConverter;
import pg.converters.DSTaskToTaskDetailConverter;
import pg.program.TaskDetail;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.window.controller.task.atomic.call.ds.ListOfTaskCall;
import pg.web.ds.detail.DSTask;
import pg.web.ds.detail.DSTaskListDetail;
import pg.web.ds.detail.DsApiDetail;

import java.util.List;

public class LiveTrackRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LiveTrackRunnable.class);

    private final String sid;
    private final TableView<TaskDetail> tableView;
    protected final DsApiDetail dsApiDetail;

    public LiveTrackRunnable(String sid, TableView<TaskDetail> tableView, DsApiDetail dsApiDetail) {
        this.sid = sid;
        this.tableView = tableView;
        this.dsApiDetail = dsApiDetail;
    }

    @Override
    public void run() {
        while (true) {
            try {
                logger.info("Task list refresh.");
                DSTaskListDetail taskList = getDsTaskListDetail();
                updateUIView(taskList);
                long liveTrackInterval = ApplicationPropertiesHelper.getInstance().getLiveTrackInterval();
                Thread.sleep(liveTrackInterval * 1000);
            } catch (InterruptedException  e) {
                logger.info("Live tracking ended.");
                break;
            }
        }
    }


    protected DSTaskListDetail getDsTaskListDetail() {
        ListOfTaskCall listOfTaskCall = new ListOfTaskCall(sid, dsApiDetail.getDownloadStationTask());
        return listOfTaskCall.call();
    }

    protected void updateUIView(DSTaskListDetail listOfTasks) {
        AbstractConverter<DSTask, TaskDetail> converter = new DSTaskToTaskDetailConverter();
        List<TaskDetail> tasks = converter.convert(listOfTasks.getTasks());
        if (tasks.isEmpty()) {
            tasks.add(TaskDetail.getNothingToDisplay());
        }
        if (Platform.isFxApplicationThread()) {
            tableView.setItems(FXCollections.observableList(tasks));
            tableView.requestFocus();
        } else {
            Platform.runLater(() -> {
                tableView.setItems(FXCollections.observableList(tasks));
                tableView.requestFocus();
            });
        }

    }
}
