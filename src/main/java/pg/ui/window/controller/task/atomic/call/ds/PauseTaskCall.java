package pg.ui.window.controller.task.atomic.call.ds;

import pg.program.ApiName;
import pg.program.TaskDetail;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.List;

/** Created by Gawa 2017-11-11 */
class PauseTaskCall extends ManageTaskCall {

    PauseTaskCall(String sid, List<TaskDetail> tasksToPause, DSApiDetails downloadStationTask) {
        super(sid, tasksToPause, downloadStationTask);
    }

    @Override
    protected String buildTaskUrl() {
        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK +
                "&version=" + downloadStationTask.getMaxVersion() +
                "&method=" + getTaskMethod().method() +
                "&id=" + ids() +
                "&_sid=" + sid;
    }

    @Override
    protected DSTaskMethod getTaskMethod() {
        return DSTaskMethod.PAUSE;
    }
}
