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

/** Created by Gawa 2017-11-11 */
class ResumeCall extends ManageTaskCall {

    ResumeCall(String sid, List<TaskDetail> tasksToPause, DSApiDetails downloadStationTask) {
        super(sid, tasksToPause, downloadStationTask);
    }

    @Override
    public List<DSItem> call() {
        String requestUrl = buildCreateTaskUrl();
        logger.info("RestURL: '{}'.", requestUrl);

        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            return handleResponse(response.get(), DSTaskMethod.RESUME);
        }
        return Collections.emptyList();
    }

    private String buildCreateTaskUrl() {
        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK +
                "&version=" + downloadStationTask.getMaxVersion() +
                "&method=" + DSTaskMethod.RESUME.method() +
                "&id=" + ids() +
                "&_sid=" + sid;
    }
}
