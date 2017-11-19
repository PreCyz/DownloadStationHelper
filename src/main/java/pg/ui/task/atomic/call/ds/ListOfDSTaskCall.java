package pg.ui.task.atomic.call.ds;

import pg.service.ds.DSError;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.ApiDetails;
import pg.web.model.ApiName;
import pg.web.response.TaskListResponse;
import pg.web.response.detail.TaskListDetail;

import java.util.Optional;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-11-11 */
public class ListOfDSTaskCall extends DSBasic implements Callable<TaskListDetail> {

    private final String sid;
    private final ApiDetails downloadStationTask;

    public ListOfDSTaskCall(String sid, ApiDetails downloadStationTask) {
        super();
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    public TaskListDetail call() {
        return listOfTasks();
    }

    private TaskListDetail listOfTasks() {
        String requestUrl = buildTaskListUrl();
        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            Optional<TaskListResponse> jsonResponse =
                    JsonUtils.convertFromString(response.get(), TaskListResponse.class);
            if (jsonResponse.isPresent()) {
                TaskListResponse taskListResponse = jsonResponse.get();
                if (taskListResponse.isSuccess()) {
                    logger.info("Total number of tasks on download station is: {}.",
                            taskListResponse.getTaskListDetail().getTotal());
                    return taskListResponse.getTaskListDetail();
                } else {
                    String logMsg = String.format("List of tasks finished with error %d - %s.",
                            taskListResponse.getError().getCode(),
                            DSError.getTaskError(taskListResponse.getError().getCode()));
                    throw new ProgramException(UIError.LIST_OF_TASK, new IllegalArgumentException(logMsg));

                }
            } else {
                throw new ProgramException(UIError.LIST_OF_TASK,
                        new IllegalArgumentException("List of tasks with error. No details."));
            }
        }
        return null;
    }

    private String buildTaskListUrl() {
        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + "list" + "&" +
                "_sid=" + sid;
    }
}