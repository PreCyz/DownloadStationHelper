package pg.ui.window.controller.task.atomic.call.ds;

import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.ApiName;
import pg.service.ds.DSError;
import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.ds.DSGeneralResponse;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.Optional;

/** Created by Gawa 2018-02-10 */
public class CreateTaskFromLinkCall extends BasicCall implements Runnable {

    private final String sid;
    private final String uri;
    private final DSApiDetails downloadStationTask;

    public CreateTaskFromLinkCall(String sid, String uri, DSApiDetails downloadStationTask) {
        super();
        this.sid = sid;
        this.uri = uri;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    public void run() {
        createDownloadStationTasks();
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
