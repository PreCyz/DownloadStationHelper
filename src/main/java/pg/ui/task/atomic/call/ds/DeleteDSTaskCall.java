package pg.ui.task.atomic.call.ds;

import pg.service.ds.DSError;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.ApiDetails;
import pg.web.model.ApiName;
import pg.web.response.DeleteResponse;
import pg.web.response.detail.DSTask;
import pg.web.synology.DSTaskMethod;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class DeleteDSTaskCall extends DSBasic implements Callable<Void> {

    private final String sid;
    private final List<DSTask> tasksToDelete;
    private final ApiDetails downloadStationTask;

    public DeleteDSTaskCall(String sid, List<DSTask> tasksToDelete, ApiDetails downloadStationTask) {
        super();
        this.sid = sid;
        this.tasksToDelete = tasksToDelete;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    public Void call() {
        createDownloadStationTasks();
        return null;
    }

    private void createDownloadStationTasks() {
        String requestUrl = buildCreateTaskUrl();
        logger.info("RestURL: '{}'.", requestUrl);

        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            List<DeleteResponse> deleteResponse = JsonUtils.convertDeleteResponseFromString(response.get());
            if (!deleteResponse.isEmpty()) {
                for(DeleteResponse detail : deleteResponse) {
                    Optional<DSTask> dsTask = tasksToDelete.stream()
                            .filter(task -> task.getId().equals(detail.getId()))
                            .findFirst();
                    if (detail.getError() == 0) {
                        dsTask.ifPresent(task->logger.info("Task '{}' deleted.", task.getTitle()));
                    } else {
                        final String logMsg = String.format("Task '{}' deleted with error %d - %s.",
                                detail.getError(),
                                DSError.getTaskError(detail.getError()));
                        dsTask.ifPresent(task->logger.info(logMsg, task.getTitle()));
                    }
                }
            } else {
                throw new ProgramException(UIError.DELETE_TASK,
                        new IllegalArgumentException("Task creation with error. No details."));
            }
        }
    }

    private String buildCreateTaskUrl() {
        String id = String.join(",", tasksToDelete.stream().map(DSTask::getId).collect(Collectors.toList()));

        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + DSTaskMethod.DELETE.method() + "&" +
                "id=" + id +
                "_sid=" + sid + "&" +
                "force_complete=true";
    }
}
