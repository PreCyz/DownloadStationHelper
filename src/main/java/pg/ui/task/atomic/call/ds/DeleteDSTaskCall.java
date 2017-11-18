package pg.ui.task.atomic.call.ds;

import pg.service.ds.DSError;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.ApiDetails;
import pg.web.model.ApiName;
import pg.web.response.DeleteItem;
import pg.web.response.DeleteResponse;
import pg.web.response.detail.DSTask;
import pg.web.synology.DSTaskMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class DeleteDSTaskCall extends DSBasic implements Callable<List<String>> {

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
    public List<String> call() {
        return createDownloadStationTasks();
    }

    private List<String> createDownloadStationTasks() {
        String requestUrl = buildCreateTaskUrl();
        logger.info("RestURL: '{}'.", requestUrl);

        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            Optional<DeleteResponse> deleteResponse = JsonUtils.convertFromString(response.get(), DeleteResponse.class);
            if (deleteResponse.isPresent() && deleteResponse.get().isSuccess()) {
                List<DeleteItem> deleteItems = deleteResponse.map(DeleteResponse::getDeletedItems).get();
                List<String> result = new ArrayList<>();
                for (DeleteItem item : deleteItems) {
                    if (item.getError() == 0) {
                        logger.info("Task '{}' deleted.", item.getId());
                        result.add(item.getId());
                    } else {
                        final String logMsg = String.format("Task '%s' not deleted. Error %d - %s.", item.getId(),
                                item.getError(), DSError.getTaskError(item.getError()));
                        logger.info(logMsg);
                    }
                }
                return result;
            } else {
                throw new ProgramException(UIError.DELETE_TASK,
                        new RuntimeException("Task creation with error. No details."));
            }
        }
        return Collections.emptyList();
    }

    private String buildCreateTaskUrl() {
        String id = String.join(",", tasksToDelete.stream().map(DSTask::getId).collect(Collectors.toList()));

        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + DSTaskMethod.DELETE.method() + "&" +
                "id=" + id + "&" +
                //"force_complete=true&" +
                "_sid=" + sid;
    }
}
