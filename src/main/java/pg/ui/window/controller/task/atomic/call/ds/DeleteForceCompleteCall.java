package pg.ui.window.controller.task.atomic.call.ds;

import pg.web.ds.DSDeletedItem;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;
import pg.web.ds.detail.DSTask;
import pg.web.model.ApiName;

import java.util.List;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class DeleteForceCompleteCall extends DeleteCall {

    public DeleteForceCompleteCall(String sid, List<DSTask> tasksToDelete, DSApiDetails downloadStationTask) {
        super(sid, tasksToDelete, downloadStationTask);
        this.operation = "cleaned";
    }

    @Override
    public List<DSDeletedItem> call() {
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
