package pg.ui.window.controller.completable;

import pg.program.SearchItem;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactory;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactoryBean;
import pg.ui.window.controller.task.atomic.call.torrent.WriteMatchTorrentsCall;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DsApiDetail;
import pg.web.torrent.ReducedDetail;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.stream.Collectors.toList;

public class StartDownloadCompletable implements Runnable {

    private List<SearchItem> itemsToDownload;
    private DsApiDetail dsApiDetail;
    private ExecutorService executor;

    public StartDownloadCompletable(List<SearchItem> itemsToDownload, DsApiDetail dsApiDetail, ExecutorService executor) {
        this.itemsToDownload = itemsToDownload;
        this.dsApiDetail = dsApiDetail;
        this.executor = executor;
    }

    @Override
    public void run() {
        CompletableFuture.supplyAsync(this::convertSearchItems, executor)
                .thenApply(this::createTask)
                .thenAccept(WriteMatchTorrentsCall::new);
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

}
