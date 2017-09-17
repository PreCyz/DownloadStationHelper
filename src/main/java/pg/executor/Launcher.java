package pg.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.service.*;
import pg.util.PropertyLoader;
import pg.web.model.DSMethod;
import pg.web.model.SettingKeys;
import pg.web.response.TorrentResponse;

import java.util.List;
import java.util.Properties;

/**Created by Gawa 2017-09-15*/
public class Launcher implements Runnable {

    private static final Logger logger = LogManager.getLogger(Launcher.class);

    private final Properties application;

    public Launcher() {
        this.application = PropertyLoader.getApplicationProperties();
    }

    @Override
    public void run() {
        TorrentService torrentService = new TorrentServiceImpl();
        List<TorrentResponse> torrents = torrentService.findTorrents();
        FileService fileService = new FileServiceImpl();
        fileService.buildImdbMap(torrents);
        fileService.writeImdbMapToFile();
        MatchService matchService = new MatchServiceImpl();
        matchService.filterTorrents(torrents);
        if (matchService.hasFoundMatchingTorrents()) {
            final String notGiven = "NOT_GIVEN";
            final String filePath = application.getProperty(SettingKeys.FILE_PATH.key(), notGiven);
            if (!notGiven.equals(filePath.trim())) {
                fileService.writeTorrentsToFile(matchService.getMatchingTorrents());
            }
            String creationMethod = application.getProperty(
                    SettingKeys.CREATION_METHOD.key(),
                    DSMethod.COPY_FILE.name()
            );
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
