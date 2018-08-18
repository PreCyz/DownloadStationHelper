package pg.ui.window.controller.task.atomic.call.ds;

import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.TaskDetail;
import pg.service.ds.DSError;
import pg.util.JsonUtils;
import pg.web.ds.DSDataResponse;
import pg.web.ds.DSItem;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public abstract class ManageTaskCall extends BasicCall implements Callable<List<DSItem>> {

    protected final String sid;
    final List<TaskDetail> tasksToChange;
    protected final DSApiDetails downloadStationTask;

    ManageTaskCall(String sid, List<TaskDetail> tasksToChange, DSApiDetails downloadStationTask) {
        super();
        this.sid = sid;
        this.tasksToChange = tasksToChange;
        this.downloadStationTask = downloadStationTask;
    }

    List<DSItem> handleResponse(String response, DSTaskMethod taskMethod) {
        Optional<DSDataResponse> dsResponse = JsonUtils.convertFromString(response, DSDataResponse.class);
        if (dsResponse.isPresent() && dsResponse.get().isSuccess()) {
            List<DSItem> DSItems = dsResponse.map(DSDataResponse::getData).get();
            for (DSItem item : DSItems) {
                if (item.getError() == 0) {
                    logger.info("Task '{}' {}.", item.getId(), taskMethod);
                } else {
                    final String logMsg = String.format("Task '%s' not %s. Error %d - %s.", item.getId(), taskMethod.method(),
                            item.getError(), DSError.getTaskError(item.getError()));
                    logger.info(logMsg);
                }
            }
            return DSItems;
        } else {
            logger.error(DSError.getTaskError(dsResponse.get().getError().getCode()));
            throw new ProgramException(getUIError(taskMethod),
                    new RuntimeException(String.format("Task [%s] with error. No details.", taskMethod.method())));
        }
    }

    private UIError getUIError(DSTaskMethod taskMethod) {
        switch (taskMethod) {
            case PAUSE:
                return UIError.PAUSE_TASK;
            case DELETE:
                return UIError.DELETE_TASK;
            default:
                return UIError.LAUNCH_PROGRAM;
        }
    }

    String ids() {
        return String.join(",", tasksToChange.stream().map(TaskDetail::getId).collect(Collectors.toList()));
    }
}
