package pg.ui.task;

import javafx.concurrent.Task;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.ds.AvailableOperationDSCall;
import pg.ui.task.atomic.call.ds.DsApiDetail;
import pg.ui.task.atomic.call.ds.LoginDSCall;

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
        availableOperation = new AppTask<>(new AvailableOperationDSCall(), executor);
        loginToDs = new AppTask<>(new LoginDSCall(availableOperation.get().getAuthInfo()), executor);
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
