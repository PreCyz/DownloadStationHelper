package pg.ui.task.atomic.call.ds;

import pg.service.ds.DSError;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.model.ApiDetails;
import pg.web.model.ApiName;
import pg.web.model.TorrentUrlType;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.GeneralResponse;
import pg.web.synology.DSTaskMethod;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Created by Gawa 2017-11-11 */
public class CreateCall extends BasicCall implements Callable<Void> {

    private final String sid;
    private final List<ReducedDetail> matchTorrents;
    private final ApiDetails downloadStationTask;

    public CreateCall(String sid, List<ReducedDetail> matchTorrents, ApiDetails downloadStationTask) {
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
            Optional<GeneralResponse> jsonResponse =
                    JsonUtils.convertFromString(response.get(), GeneralResponse.class);
            if (jsonResponse.isPresent()) {
                GeneralResponse createTaskResponse = jsonResponse.get();
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
