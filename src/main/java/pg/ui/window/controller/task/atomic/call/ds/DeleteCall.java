package pg.ui.window.controller.task.atomic.call.ds;

import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.service.ds.DSError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.ApiDetails;
import pg.web.model.ApiName;
import pg.web.response.DSDeleteResponse;
import pg.web.response.DeleteItem;
import pg.web.response.detail.DSTask;
import pg.web.synology.DSTaskMethod;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class DeleteCall extends BasicCall implements Callable<List<DeleteItem>> {

    protected final String sid;
    protected final List<DSTask> tasksToDelete;
    protected final ApiDetails downloadStationTask;
    protected String operation;

    public DeleteCall(String sid, List<DSTask> tasksToDelete, ApiDetails downloadStationTask) {
        super();
        this.sid = sid;
        this.tasksToDelete = tasksToDelete;
        this.downloadStationTask = downloadStationTask;
        this.operation = "deleted";
    }

    @Override
    public List<DeleteItem> call() {
        return deleteDownloadStationTasks();
    }

    private List<DeleteItem> deleteDownloadStationTasks() {
        String requestUrl = buildCreateTaskUrl();
        logger.info("RestURL: '{}'.", requestUrl);

        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            Optional<DSDeleteResponse> deleteResponse = JsonUtils.convertFromString(response.get(), DSDeleteResponse.class);
            if (deleteResponse.isPresent() && deleteResponse.get().isSuccess()) {
                List<DeleteItem> deleteItems = deleteResponse.map(DSDeleteResponse::getDeletedItems).get();
                for (DeleteItem item : deleteItems) {
                    if (item.getError() == 0) {
                        logger.info("Task '{}' {}.", item.getId(), operation);
                    } else {
                        final String logMsg = String.format("Task '%s' not %s. Error %d - %s.", item.getId(), operation,
                                item.getError(), DSError.getTaskError(item.getError()));
                        logger.info(logMsg);
                    }
                }
                return deleteItems;
            } else {
                logger.error(DSError.getTaskError(deleteResponse.get().getError().getCode()));
                throw new ProgramException(UIError.DELETE_TASK,
                        new RuntimeException(String.format("Task [%s] with error. No details.", operation)));
            }
        }
        return Collections.emptyList();
    }

    protected String buildCreateTaskUrl() {
        String id = String.join(",", tasksToDelete.stream().map(DSTask::getId).collect(Collectors.toList()));

        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + DSTaskMethod.DELETE.method() + "&" +
                "id=" + id + "&" +
                "force_complete=false&" +
                "_sid=" + sid;
    }
}
