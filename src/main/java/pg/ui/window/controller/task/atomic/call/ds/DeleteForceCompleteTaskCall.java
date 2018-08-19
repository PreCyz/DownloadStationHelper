package pg.ui.window.controller.task.atomic.call.ds;

import pg.program.ApiName;
import pg.program.TaskDetail;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.List;

/** Created by Gawa 2017-11-11 */
class DeleteForceCompleteTaskCall extends ManageTaskCall {

    DeleteForceCompleteTaskCall(String sid, List<TaskDetail> tasksToDelete, DSApiDetails downloadStationTask) {
        super(sid, tasksToDelete, downloadStationTask);
    }

    protected String buildTaskUrl() {
        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK +
                "&version=" + downloadStationTask.getMaxVersion() +
                "&method=" + getTaskMethod().method() +
                "&id=" + ids() +
                "&force_complete=true" +
                "&_sid=" + sid;
    }

    @Override
    protected DSTaskMethod getTaskMethod() {
        return DSTaskMethod.DELETE_FORCE;
    }
}
