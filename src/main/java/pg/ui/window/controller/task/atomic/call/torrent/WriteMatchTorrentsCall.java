package pg.ui.window.controller.task.atomic.call.torrent;

import pg.props.ApplicationPropertiesHelper;
import pg.services.FileService;
import pg.services.FileServiceImpl;
import pg.web.torrent.ReducedDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-29 */
public class WriteMatchTorrentsCall implements Callable<Void> {

    private final List<ReducedDetail> matchTorrents;
    private final ApplicationPropertiesHelper application;

    public WriteMatchTorrentsCall(List<ReducedDetail> matchTorrents) {
        this.matchTorrents = matchTorrents;
        this.application = ApplicationPropertiesHelper.getInstance();
    }

    @Override
    public Void call() throws Exception {
        final String notGiven = "NOT_GIVEN";
        final String filePath = application.getFilePath(notGiven);
        if (!notGiven.equals(filePath.trim())) {
            FileService fileService = new FileServiceImpl();
            fileService.writeTorrentsToFile(matchTorrents);
        }
        return null;
    }
}
