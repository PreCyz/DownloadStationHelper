package pg.ui.task;

import javafx.concurrent.Task;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.ds.AvailableOperationDSCall;
import pg.ui.task.atomic.call.ds.DsApiDetail;
import pg.util.AppConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class AvailableOperationTask extends Task<Void> {

    private final ExecutorService executor;
    private final Logger logger;
    private final Pane connectionPane;
    private final ApplicationPropertiesHelper application;
    private AppTask<DsApiDetail> availableOperation;

    public AvailableOperationTask(ExecutorService executor, Pane connectionPane) {
        this.executor = executor;
        this.connectionPane = connectionPane;
        this.logger = LogManager.getLogger(getClass());
        this.application = ApplicationPropertiesHelper.getInstance();
    }

    @Override
    protected Void call() throws Exception {
        updateMessage("Connecting ...");
        DsApiDetail dsApiDetail = getAvailableOperations();
        saveVersion(dsApiDetail);
        setConnectedImg();
        updateMessage("Connected");
        logger.info("Connected.");
        //Platform.runLater(this::setConnectedImg);
        return null;
    }

    private DsApiDetail getAvailableOperations() {
        availableOperation = new AppTask<>(new AvailableOperationDSCall(), executor);
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
        String connectingGif = String.format("%sconnected.gif", AppConstants.IMG_RESOURCE_PATH);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(connectingGif);
        BackgroundSize backgroundSize = new BackgroundSize(43, 43, false, false, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(inputStream),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        Background background = new Background(backgroundImage);
        Tooltip tooltip = new Tooltip();
        tooltip.setText(String.format("Connection with %s established.", application.getServerUrl()));
        connectionPane.setBackground(background);
    }

    public DsApiDetail getDsApiDetail() {
        return availableOperation == null ? null : availableOperation.get();
    }
}
