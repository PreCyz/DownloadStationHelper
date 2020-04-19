package pg.ui.window.controller.task.atomic.call.ds;

import pg.program.ApiName;
import pg.web.ds.detail.DSApiDetails;

import java.util.concurrent.Callable;

public class SearchTorrentCall extends BasicCall implements Callable<Object> {

    private final String sid;
    private final DSApiDetails downloadStationTask;

    public SearchTorrentCall(String sid, DSApiDetails downloadStationTask, String keyword) {
        super();
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    public Object call() throws Exception {
        return null;
    }

    private String buildSearchUrl() {


        return prepareServerUrl() + "/webapi/DownloadStation/btsearch.cgi" +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_BT_SEARCH + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=start" + "&" +
                "keyword=" + "detail,file" + "&" +
                "module=enabled" + "&" +
                "_sid=" + sid;
    }
}
