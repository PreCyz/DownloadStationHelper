package pg.ui.checker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.loader.ApplicationPropertiesLoader;
import pg.util.StringUtils;

/**Created by Gawa 2017-10-07*/
public class ApplicationChecker implements Checker {

    private static final Logger logger = LogManager.getLogger(ApplicationChecker.class);

    private final ApplicationPropertiesLoader loader;

    public ApplicationChecker() {
        loader = ApplicationPropertiesLoader.getInstance();
    }

    @Override
    public boolean ready() {
        if (StringUtils.nullOrTrimEmpty(loader.getUsername())) {
            logger.info("Basic configuration incomplete. Login not found.");
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getPassword())) {
            logger.info("Basic configuration incomplete. Password not found.");
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getServerUrl())) {
            logger.info("Basic configuration incomplete. Server url not found.");
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getCreationMethod(""))) {
            logger.info("Basic configuration incomplete. Task creation method not found.");
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getTorrentUrlType(""))) {
            logger.info("Basic configuration incomplete. Torrent url type not found.");
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getDestination())) {
            logger.info("Basic configuration incomplete. Synology download folder not found.");
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getApiInfo())) {
            logger.info("Basic configuration incomplete. Synology API Info not found.");
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getFilePath(""))) {
            logger.info("Basic configuration incomplete. Store location for json files not found.");
            return false;
        }
        return true;
    }
}
