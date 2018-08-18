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
public class PauseCall extends ManageTaskCall implements Callable<List<DSItem>> {

    public PauseCall(String sid, List<TaskDetail> tasksToPause, DSApiDetails downloadStationTask) {
        super(sid, tasksToPause, downloadStationTask);
    }

    @Override
    public List<DSItem> call() {
        String requestUrl = buildCreateTaskUrl();
        logger.info("RestURL: '{}'.", requestUrl);

        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            return handleResponse(response.get(), DSTaskMethod.PAUSE);
        }
        return Collections.emptyList();
    }

    private String buildCreateTaskUrl() {
        String id = String.join(",", tasksToChange.stream().map(TaskDetail::getId).collect(Collectors.toList()));

        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK +
                "&version=" + downloadStationTask.getMaxVersion() +
                "&method=" + DSTaskMethod.PAUSE.method() +
                "&id=" + id +
                "&_sid=" + sid;
    }
}
