package pg.ui.window.controller.task.atomic.call.ds;

import pg.util.JsonUtils;
import pg.web.client.GetClient;
import pg.web.response.SynologyResponse;
import pg.web.response.detail.DsApiDetail;

import java.util.Optional;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-11-11 */
public class AvailableOperationCall extends BasicCall implements Callable<DsApiDetail> {

    private DsApiDetail dsApiDetail;

    public AvailableOperationCall() {
        super();
        this.dsApiDetail = new DsApiDetail();
    }

    @Override
    public DsApiDetail call() {
        prepareAvailableOperations();
        return dsApiDetail;
    }

    private void prepareAvailableOperations() {
        String requestUrl = prepareServerUrl() + application.getApiInfo();
        GetClient client = new GetClient(requestUrl);
        Optional<String> response = client.get();
        if (response.isPresent()) {
            Optional<SynologyResponse> jsonResponse =
                    JsonUtils.convertFromString(response.get(), SynologyResponse.class);
            if (jsonResponse.isPresent()) {
                SynologyResponse synologyResponse = jsonResponse.get();
                if (synologyResponse.isSuccess()) {
                    dsApiDetail.setAuthInfo(synologyResponse.getDetailResponse().getAuthInfo());
                    dsApiDetail.setDownloadStationTask(synologyResponse.getDetailResponse().getDownloadStationTask());
                }
            }
        }
    }
}
