package pg.ui.window.controller.task.atomic.call.ds;

import pg.program.ApiName;
import pg.program.TaskDetail;
import pg.web.ds.DSItem;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.List;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class DeleteForceCompleteCall extends DeleteCall {

    public DeleteForceCompleteCall(String sid, List<TaskDetail> tasksToDelete, DSApiDetails downloadStationTask) {
        super(sid, tasksToDelete, downloadStationTask);
    }

    @Override
    public List<DSItem> call() {
        return super.call();
    }

    protected String buildCreateTaskUrl() {
        String id = String.join(",", tasksToChange.stream().map(TaskDetail::getId).collect(Collectors.toList()));

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
