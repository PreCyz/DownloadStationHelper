package pg.ui.window.controller.task;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.window.controller.task.atomic.AppTask;
import pg.ui.window.controller.task.atomic.call.ds.AvailableOperationCall;
import pg.util.AppConstants;
import pg.util.ImageUtils;
import pg.web.ds.detail.DsApiDetail;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class AvailableOperationTask extends Task<Void> {

    private final ExecutorService executor;
    private final Logger logger;
    private final Pane connectionPane;
    private Pane favouritePane;
    private Pane imdbPane;
    private AppTask<DsApiDetail> availableOperation;

    public AvailableOperationTask(ExecutorService executor, Pane connectionPane) {
        this.executor = executor;
        this.connectionPane = connectionPane;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public void setFavouritePane(Pane favouritePane) {
        this.favouritePane = favouritePane;
    }

    public void setImdbPane(Pane imdbPane) {
        this.imdbPane = imdbPane;
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
        DsApiDetail dsApiDetail = availableOperation.get();
        enablePanes();
        return dsApiDetail;
    }

    private void enablePanes() {
        if (Platform.isFxApplicationThread()) {
            if (imdbPane != null) {
                imdbPane.setDisable(false);
            }
            if (favouritePane != null) {
                favouritePane.setDisable(false);
            }
        } else {
            Platform.runLater(() -> {
                if (imdbPane != null) {
                    imdbPane.setDisable(false);
                }
                if (favouritePane != null) {
                    favouritePane.setDisable(false);
                }
            });
        }
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
        try {
            connectionPane.setBackground(ImageUtils.getBackground(AppConstants.CONNECTED_GIF));
        } catch (IOException e) {
            logger.warn("Could not load image {}.", AppConstants.CONNECTED_GIF);
        }
    }

    public DsApiDetail getDsApiDetail() {
        return availableOperation == null ? null : availableOperation.get();
    }
}
