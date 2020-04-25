package pg.ui.window.controller.completable;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.scene.layout.Background;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.program.SearchItem;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactory;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactoryBean;
import pg.ui.window.controller.task.atomic.call.torrent.WriteMatchTorrentsCall;
import pg.util.AppConstants;
import pg.util.ImageUtils;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DsApiDetail;
import pg.web.torrent.ReducedDetail;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.stream.Collectors.toList;

public class StartDownloadCompletable implements Runnable {
    private static final Logger logger = LogManager.getLogger(SearchCompletable.class);
    private final List<SearchItem> itemsToDownload;
    private final DsApiDetail dsApiDetail;
    private final ExecutorService executor;
    private final Property<Background> backgroundProperty;

    public StartDownloadCompletable(List<SearchItem> itemsToDownload, Property<Background> backgroundProperty,
                                    DsApiDetail dsApiDetail, ExecutorService executor) {
        this.itemsToDownload = itemsToDownload;
        this.backgroundProperty = backgroundProperty;
        this.dsApiDetail = dsApiDetail;
        this.executor = executor;
    }

    @Override
    public void run() {
        CompletableFuture.supplyAsync(this::convertSearchItems, executor)
                .thenApply(this::createTask)
                .thenAccept(WriteMatchTorrentsCall::new)
                .thenRun(this::updateProgressImage);
    }

    private List<ReducedDetail> createTask(List<ReducedDetail> torrents) {
        ManageTaskFactoryBean factoryBean = new ManageTaskFactoryBean(
                dsApiDetail.getSid(),
                dsApiDetail.getDownloadStationTask(),
                DSTaskMethod.CREATE,
                torrents
        );
        ManageTaskFactory.getManageTask(factoryBean).call();
        return torrents;
    }

    private List<ReducedDetail> convertSearchItems() {
        return itemsToDownload.stream().map(ReducedDetail::valueFrom).collect(toList());
    }

    private void updateProgressImage() {
        Platform.runLater(() -> {
            try {
                final double width = 1.5;
                final double height = 1.5;
                backgroundProperty.setValue(ImageUtils.getBackground(AppConstants.FINGER_UP_GIF, width, height));
                logger.info("Image changed to completed.");
            } catch (IOException e) {
                logger.warn("Could not load progress gif.");
            }
        });
    }

}
