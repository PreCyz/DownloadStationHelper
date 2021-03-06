package pg.ui.window.controller.task.atomic.call.ds;

import pg.exceptions.ProgramException;
import pg.exceptions.UIError;
import pg.program.ApiName;
import pg.services.ds.DSError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.DSTaskListResponse;
import pg.web.ds.detail.DSApiDetails;
import pg.web.ds.detail.DSTaskListDetail;

import java.util.Optional;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-11-11 */
public class ListOfTaskCall extends BasicCall implements Callable<DSTaskListDetail> {

    private final String sid;
    private final DSApiDetails downloadStationTask;

    public ListOfTaskCall(String sid, DSApiDetails downloadStationTask) {
        super();
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    public DSTaskListDetail call() {
        return listOfTasks();
    }

    private DSTaskListDetail listOfTasks() {
        String requestUrl = buildTaskListUrl();
        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            Optional<DSTaskListResponse> jsonResponse =
                    JsonUtils.convertFromString(response.get(), DSTaskListResponse.class);
            if (jsonResponse.isPresent()) {
                DSTaskListResponse taskListResponse = jsonResponse.get();
                if (taskListResponse.isSuccess()) {
                    logger.info("Total number of tasks on download station is: {}.",
                            taskListResponse.getDSTaskListDetail().getTotal());
                    return taskListResponse.getDSTaskListDetail();
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
                "additional=" + "detail,file" + "&" +
                "_sid=" + sid;
    }
}
