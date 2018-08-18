package pg.ui.window.controller.task.atomic.call.ds;

import pg.program.ApiName;
import pg.program.TaskDetail;
import pg.web.client.GetClient;
import pg.web.ds.DSItem;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class DeleteCall extends ManageTaskCall implements Callable<List<DSItem>> {

    public DeleteCall(String sid, List<TaskDetail> tasksToChange, DSApiDetails downloadStationTask) {
        super(sid, tasksToChange, downloadStationTask);
    }

    @Override
    public List<DSItem> call() {
        String requestUrl = buildCreateTaskUrl();
        logger.info("RestURL: '{}'.", requestUrl);

        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            return handleResponse(response.get(), DSTaskMethod.DELETE);
        }
        return Collections.emptyList();
    }

    protected String buildCreateTaskUrl() {
        String id = String.join(",", tasksToChange.stream().map(TaskDetail::getId).collect(Collectors.toList()));

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
