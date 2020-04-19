package pg.ui.window.controller.task.atomic.call.ds;

import pg.exceptions.ProgramException;
import pg.exceptions.UIError;
import pg.program.TaskDetail;
import pg.service.ds.DSError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.DSDataResponse;
import pg.web.ds.DSItem;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public abstract class ManageTaskCall extends BasicCall implements Callable<List<DSItem>> {

    protected final String sid;
    private final List<TaskDetail> tasks;
    protected final DSApiDetails downloadStationTask;

    ManageTaskCall(String sid, List<TaskDetail> tasks, DSApiDetails downloadStationTask) {
        super();
        this.sid = sid;
        this.tasks = tasks;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    public List<DSItem> call() {
        String requestUrl = buildTaskUrl();
        logger.info("RestURL: '{}'.", requestUrl);

        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            return handleResponse(response.get(), getTaskMethod());
        }
        return Collections.emptyList();
    }

    private List<DSItem> handleResponse(String response, DSTaskMethod taskMethod) {
        Optional<DSDataResponse> dsResponse = JsonUtils.convertFromString(response, DSDataResponse.class);
        if (dsResponse.isPresent() && dsResponse.get().isSuccess()) {
            List<DSItem> DSItems = dsResponse.filter(item -> Objects.nonNull(item.getData()))
                    .map(DSDataResponse::getData)
                    .orElseGet(ArrayList::new);
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
            if (dsResponse.isPresent()) {
                logger.error(DSError.getTaskError(dsResponse.get().getError().getCode()));
            } else {
                logger.error("Can not retrieve error code.");
            }
            throw new ProgramException(getUIError(taskMethod),
                    new RuntimeException(String.format("Task [%s] with error. No details.", taskMethod.method())));
        }
    }

    private UIError getUIError(DSTaskMethod taskMethod) {
        switch (taskMethod) {
            case CREATE:
                return UIError.CREATE_TASK;
            case CREATE_FROM_LINK:
                return UIError.CREATE_TASK_FROM_LINK;
            case PAUSE:
                return UIError.PAUSE_TASK;
            case RESUME:
                return UIError.RESUME_TASK;
            case DELETE:
                return UIError.DELETE_TASK;
            case DELETE_FORCE:
                return UIError.DELETE_FORCE_TASK;
            default:
                return UIError.LAUNCH_PROGRAM;
        }
    }

    String ids() {
        return String.join(",", tasks.stream().map(TaskDetail::getId).collect(Collectors.toList()));
    }

    protected abstract String buildTaskUrl();
    protected abstract DSTaskMethod getTaskMethod();
}
