package pg.ui.task;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.ds.AvailableOperationCall;
import pg.util.AppConstants;
import pg.web.response.detail.DsApiDetail;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class AvailableOperationTask extends Task<Void> {

    private final ExecutorService executor;
    private final Logger logger;
    private final Pane connectionPane;
    private AppTask<DsApiDetail> availableOperation;

    public AvailableOperationTask(ExecutorService executor, Pane connectionPane) {
        this.executor = executor;
        this.connectionPane = connectionPane;
        this.logger = LogManager.getLogger(getClass());
    }

    @Override
    protected Void call() throws Exception {
        updateMessage("Connecting ...");
        DsApiDetail dsApiDetail = getAvailableOperations();
        saveVersion(dsApiDetail);
        setConnectedImg();
        updateMessage("Connected");
        logger.info("Connected.");
        return null;
    }

    private DsApiDetail getAvailableOperations() {
        availableOperation = new AppTask<>(new AvailableOperationCall(), executor);
        return availableOperation.get();
    }

    private void saveVersion(DsApiDetail dsApiDetail) throws IOException {
        ApplicationPropertiesHelper application = ApplicationPropertiesHelper.getInstance();
        if (application.getApiVersion() == 0) {
            int maxVersion = dsApiDetail.getAuthInfo().getMaxVersion();
            application.storeApiVersion(String.valueOf(maxVersion));
            logger.info("Current Disk Station API version '{}' stored in application.properties", maxVersion);
        }
    }

    private void setConnectedImg() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(AppConstants.CONNECTED_GIF);
        BackgroundSize backgroundSize = new BackgroundSize(
                connectionPane.getWidth(),
                connectionPane.getHeight(),
                false,
                false,
                false,
                false
        );
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(inputStream),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        Background background = new Background(backgroundImage);
        connectionPane.setBackground(background);
    }

    public DsApiDetail getDsApiDetail() {
        return availableOperation == null ? null : availableOperation.get();
    }
}
