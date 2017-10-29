package pg.ui.task.atomic.call;

import pg.props.ApplicationPropertiesHelper;
import pg.service.FileService;
import pg.service.FileServiceImpl;
import pg.web.model.torrent.ReducedDetail;

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
