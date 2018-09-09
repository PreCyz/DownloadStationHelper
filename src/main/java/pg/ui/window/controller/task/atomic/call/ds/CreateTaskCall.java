package pg.ui.window.controller.task.atomic.call.ds;

import pg.program.ApiName;
import pg.program.TorrentUrlType;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;
import pg.web.torrent.ReducedDetail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
class CreateTaskCall extends ManageTaskCall {

    private final List<ReducedDetail> matchTorrents;

    CreateTaskCall(String sid, List<ReducedDetail> matchTorrents, DSApiDetails downloadStationTask) {
        super(sid, Collections.emptyList(), downloadStationTask);
        this.matchTorrents = matchTorrents;
    }

    protected String buildTaskUrl() {
        String uri = matchTorrents.stream()
                .map(this::getEncodedTorrentUri)
                .collect(Collectors.joining(","));

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

    private String getEncodedTorrentUri(ReducedDetail reducedDetail) {
        final TorrentUrlType urlType = TorrentUrlType.valueOf(
                application.getTorrentUrlType(TorrentUrlType.torrent.name())
        );
        switch (urlType) {
            case magnet:
                try {
                    return URLEncoder.encode(reducedDetail.getMagnetUrl(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return reducedDetail.getMagnetUrl();
                }
            default:
                try {
                    return URLEncoder.encode(reducedDetail.getTorrentUrl(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return reducedDetail.getTorrentUrl();
                }
        }
    }

    @Override
    protected DSTaskMethod getTaskMethod() {
        return DSTaskMethod.CREATE;
    }
}
