package pg.ui.task.atomic.call.ds;

import pg.web.model.ApiDetails;
import pg.web.model.ApiName;
import pg.web.response.DeleteItem;
import pg.web.response.detail.DSTask;
import pg.web.synology.DSTaskMethod;

import java.util.List;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class DeleteForceCompleteCall extends DeleteCall {

    public DeleteForceCompleteCall(String sid, List<DSTask> tasksToDelete, ApiDetails downloadStationTask) {
        super(sid, tasksToDelete, downloadStationTask);
        this.operation = "cleaned";
    }

    @Override
    public List<DeleteItem> call() {
        return super.call();
    }

    protected String buildCreateTaskUrl() {
        String id = String.join(",", tasksToDelete.stream().map(DSTask::getId).collect(Collectors.toList()));

        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + DSTaskMethod.DELETE.method() + "&" +
                "id=" + id + "&" +
                "force_complete=true&" +
                "_sid=" + sid;
    }
}
