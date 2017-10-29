package pg.ui.task.atomic.call;

import pg.props.ApplicationPropertiesHelper;
import pg.service.DiskStationService;
import pg.service.DiskStationServiceImpl;
import pg.web.model.DSMethod;
import pg.web.model.torrent.ReducedDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-29 */
public class StartTorrentsCall implements Callable<Void> {

    private final ApplicationPropertiesHelper application;
    private final List<ReducedDetail> torrents;

    public StartTorrentsCall(List<ReducedDetail> torrents) {
        this.application = ApplicationPropertiesHelper.getInstance();
        this.torrents = torrents;
    }

    @Override
    public Void call() throws Exception {
        String creationMethod = application.getCreationMethod(DSMethod.COPY_FILE.name());
        DiskStationService dsService = new DiskStationServiceImpl(torrents);
        switch (DSMethod.valueOf(creationMethod)) {
            case COPY_FILE:
                dsService.writeTorrentsOnDS();
                break;
            case REST:
                dsService.prepareAvailableOperations();
                dsService.loginToDiskStation();
                dsService.createDownloadStationTasks();
                dsService.listOfTasks();
                dsService.logoutFromDiskStation();
                break;
        }
        return null;
    }
}
