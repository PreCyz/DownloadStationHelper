package pg.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.program.ProgramMode;
import pg.props.ApplicationPropertiesHelper;
import pg.service.FileService;
import pg.service.FileServiceImpl;
import pg.service.ds.DiskStationService;
import pg.service.ds.DiskStationServiceImpl;
import pg.service.match.MatchService;
import pg.service.match.MatchServiceFactory;
import pg.service.torrent.TorrentService;
import pg.service.torrent.TorrentServiceFactory;
import pg.web.ds.detail.DSMethod;
import pg.web.torrent.TorrentDetail;

import java.util.List;

/**Created by Pawel Gawedzki on 9/21/2017.*/
public class Launcher implements Runnable {
    private static final Logger logger = LogManager.getLogger(Launcher.class);

    private final ProgramMode programMode;
    private final ApplicationPropertiesHelper application;

    public Launcher(ProgramMode programMode) {
        this.programMode = programMode;
        this.application = ApplicationPropertiesHelper.getInstance();
    }

    @Override
    public void run() {
        TorrentService torrentService = TorrentServiceFactory.getTorrentService(programMode);
        MatchService matchService = MatchServiceFactory.getMatchService(programMode);
        List<TorrentDetail> torrents = torrentService.findTorrents();
        FileService fileService = new FileServiceImpl();
        fileService.buildImdbMap(torrents);
        fileService.writeImdbMapToFile();
        matchService.match(torrents);
        if (matchService.hasFoundMatchingTorrents()) {
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
