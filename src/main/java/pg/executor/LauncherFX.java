package pg.executor;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.factory.MatchServiceFactory;
import pg.factory.TorrentServiceFactory;
import pg.props.ApplicationPropertiesHelper;
import pg.service.DiskStationService;
import pg.service.DiskStationServiceImpl;
import pg.service.FileService;
import pg.service.FileServiceImpl;
import pg.service.match.MatchService;
import pg.service.torrent.TorrentService;
import pg.util.StringUtils;
import pg.web.model.DSMethod;
import pg.web.model.ProgramMode;
import pg.web.model.torrent.ReducedDetail;
import pg.web.model.torrent.TorrentDetail;

import java.util.List;

/** Created by Gawa 2017-10-28 */
public class LauncherFX implements Runnable {

    private static final Logger logger = LogManager.getLogger(LauncherFX.class);

    private String imdbId;
    private final ProgramMode programMode;
    private final ApplicationPropertiesHelper application;
    private final ListView<ReducedDetail> torrentListView;

    public LauncherFX(ListView<ReducedDetail> torrentListView) {
        this.torrentListView = torrentListView;
        this.programMode = ProgramMode.ALL_CONCURRENT;
        application = ApplicationPropertiesHelper.getInstance();
    }

    public LauncherFX(String imdbId, ListView<ReducedDetail> torrentListView) {
        this.torrentListView = torrentListView;
        this.programMode = ProgramMode.IMDB;
        this.imdbId = imdbId;
        application = ApplicationPropertiesHelper.getInstance();
    }

    @Override
    public void run() {
        TorrentService torrentService;
        if (StringUtils.nullOrTrimEmpty(imdbId)) {
            torrentService = TorrentServiceFactory.getTorrentService(programMode);
        } else {
            torrentService = TorrentServiceFactory.getTorrentService(imdbId);
        }
        MatchService matchService = MatchServiceFactory.getMatchService(programMode);
        List<TorrentDetail> torrents = torrentService.findTorrents();
        FileService fileService = new FileServiceImpl();
        fileService.buildImdbMap(torrents);
        fileService.writeImdbMapToFile();
        matchService.match(torrents);
        if (matchService.hasFoundMatchingTorrents()) {
            torrentListView.setItems(FXCollections.observableList(matchService.getMatchingTorrents()));
            torrentListView.refresh();
            final String notGiven = "NOT_GIVEN";
            final String filePath = application.getFilePath(notGiven);
            if (!notGiven.equals(filePath.trim())) {
                fileService.writeTorrentsToFile(matchService.getMatchingTorrents());
            }
            String creationMethod = application.getCreationMethod(DSMethod.COPY_FILE.name());
            DiskStationService dsService = new DiskStationServiceImpl(matchService.getMatchingTorrents());
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
        } else {
            logger.info("No matching torrents found.");
        }
    }
}
