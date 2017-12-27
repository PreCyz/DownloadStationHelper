package pg.ui.window.controller.task;

import javafx.concurrent.Task;
import pg.ui.window.controller.task.atomic.AppTask;
import pg.ui.window.controller.task.atomic.call.ds.AvailableOperationCall;
import pg.ui.window.controller.task.atomic.call.ds.LoginCall;
import pg.web.response.detail.DsApiDetail;

import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class LoginToDSTask extends Task<Void> {

    private AppTask<DsApiDetail> availableOperation;
    private AppTask<String> loginToDs;
    private ExecutorService executor;

    public LoginToDSTask(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    protected Void call() throws Exception {
        availableOperation = new AppTask<>(new AvailableOperationCall(), executor);
        loginToDs = new AppTask<>(new LoginCall(availableOperation.get().getAuthInfo()), executor);
        loginToDs.get();
        return null;
    }

    public String getSid() {
        return loginToDs == null ? "" : loginToDs.get();
    }

    public DsApiDetail getDsApiDetail() {
        return availableOperation == null ? null : availableOperation.get();
    }


}
