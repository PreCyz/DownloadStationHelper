package pg.ui.window.controller.task.atomic.call.ds;

import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.ApiName;
import pg.program.TorrentUrlType;
import pg.service.ds.DSError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.DSGeneralResponse;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;
import pg.web.torrent.ReducedDetail;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class CreateTaskCall extends BasicCall implements Callable<Void> {

    private final String sid;
    private final List<ReducedDetail> matchTorrents;
    private final DSApiDetails downloadStationTask;

    public CreateTaskCall(String sid, List<ReducedDetail> matchTorrents, DSApiDetails downloadStationTask) {
        super();
        this.sid = sid;
        this.matchTorrents = matchTorrents;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    public Void call() {
        createDownloadStationTasks();
        return null;
    }

    private void createDownloadStationTasks() {
        String requestUrl = buildCreateTaskUrl();
        logger.info("RestURL: '{}'.", requestUrl);

        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            Optional<DSGeneralResponse> jsonResponse =
                    JsonUtils.convertFromString(response.get(), DSGeneralResponse.class);
            if (jsonResponse.isPresent()) {
                DSGeneralResponse createTaskResponse = jsonResponse.get();
                if (createTaskResponse.isSuccess()) {
                    logger.info("Task creation successful.");
                } else {
                    String logMsg = String.format("Task creation finished with error %d - %s.",
                            createTaskResponse.getError().getCode(),
                            DSError.getTaskError(createTaskResponse.getError().getCode()));
                    throw new ProgramException(UIError.CREATE_TASK, new IllegalArgumentException(logMsg));
                }
            } else {
                throw new ProgramException(UIError.CREATE_TASK,
                        new IllegalArgumentException("Task creation with error. No details."));
            }
        }
    }

    private String buildCreateTaskUrl() {
        final TorrentUrlType urlType = TorrentUrlType.valueOf(
                application.getTorrentUrlType(TorrentUrlType.torrent.name())
        );
        Function<ReducedDetail, String> extractUrlType = reducedDetail -> {
            switch (urlType) {
                case magnet:
                    return reducedDetail.getMagnetUrl();
                default:
                    return reducedDetail.getTorrentUrl();
            }
        };
        String uri = String.join(",", matchTorrents.stream()
                .map(extractUrlType)
                .collect(Collectors.toList())
        );

        String destination = application.getDestination();

        return prepareServerUrl() + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + DSTaskMethod.CREATE.method() + "&" +
                "_sid=" + sid + "&" +
                "destination=" + destination + "&" +
                "uri=" + uri;
    }
}
