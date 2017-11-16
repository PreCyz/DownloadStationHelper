package pg.ui.task;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.ds.AvailableOperationDSCall;
import pg.ui.task.atomic.call.ds.DsApiDetail;

import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class AvailableOperationTask extends Task<Void> {

    private final ExecutorService executor;
    private final Logger logger;
    private AppTask<DsApiDetail> availableOperation;

    public AvailableOperationTask(ExecutorService executor) {
        this.executor = executor;
        this.logger = LogManager.getLogger(getClass());
    }

    @Override
    protected Void call() throws Exception {
        availableOperation = new AppTask<>(new AvailableOperationDSCall(), executor);
        DsApiDetail dsApiDetail = availableOperation.get();
        ApplicationPropertiesHelper application = ApplicationPropertiesHelper.getInstance();
        if (application.getApiVersion() == 0) {
            int maxVersion = dsApiDetail.getAuthInfo().getMaxVersion();
            application.storeApiVersion(String.valueOf(maxVersion));
            logger.info("Current Disk Station API version '{}' stored in application.properties", maxVersion);
        }
        return null;
    }

    public DsApiDetail getDsApiDetail() {
        return availableOperation == null ? null : availableOperation.get();
    }
}
