package pg.ui.window.controller.task.atomic.call.ds;

import pg.program.ApiName;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.Collections;

/** Created by Gawa 2018-02-10 */
class CreateTaskFromLinkCall extends ManageTaskCall {

    private final String uri;

    CreateTaskFromLinkCall(String sid, String uri, DSApiDetails downloadStationTask) {
        super(sid, Collections.emptyList(), downloadStationTask);
        this.uri = uri;
    }

    @Override
    protected String buildTaskUrl() {
        String destination = application.getDestination();

        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK +
                "&version=" + downloadStationTask.getMaxVersion() +
                "&method=" + getTaskMethod().method() +
                "&_sid=" + sid +
                "&destination=" + destination +
                "&uri=" + uri;
    }

    @Override
    protected DSTaskMethod getTaskMethod() {
        return DSTaskMethod.CREATE_FROM_LINK;
    }
}
