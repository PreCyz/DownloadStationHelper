package pg.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.service.*;
import pg.web.model.DSMethod;
import pg.web.model.SettingKeys;

import java.util.Properties;

/**Created by Gawa 2017-09-15*/
public class Launcher implements Runnable {

    private static final Logger logger = LogManager.getLogger(Launcher.class);

    private final Properties application;
    private final TorrentService torrentService;

    public Launcher(Properties application, Properties shows) {
        this.application = application;
        this.torrentService = new TorrentServiceImpl(application, shows);
    }

    @Override
    public void run() {
        torrentService.findTorrents();
        torrentService.matchTorrents();
        if (torrentService.hasFoundTorrents()) {
            final String notGiven = "NOT_GIVEN";
            final String filePath = application.getProperty(SettingKeys.FILE_PATH.key(), notGiven);
            if (!notGiven.equals(filePath.trim())) {
                FileService fileService = new FileServiceImpl(application);
                fileService.writeTorrentsToFile(torrentService.getFoundTorrents());
                fileService.buildImdbMap(torrentService.getTorrentResponses());
                fileService.writeImdbMapToFile();
            }
            String creationMethod = application.getProperty(SettingKeys.CREATION_METHOD.key(),
                    DSMethod.COPY_FILE.name());
            DiskStationService diskStationService = new DiskStationServiceImpl(application, torrentService.getFoundTorrents());
            switch (DSMethod.valueOf(creationMethod)) {
                case COPY_FILE:
                    diskStationService.writeTorrentsOnDS();
                    break;
                case REST:
                    diskStationService.prepareAvailableOperations();
                    diskStationService.loginToDiskStation();
                    diskStationService.createDownloadStationTasks();
                    diskStationService.listOfTasks();
                    diskStationService.logoutFromDiskStation();
                    break;
            }
        } else {
            logger.info("No matching torrents found.");
        }
    }
}
