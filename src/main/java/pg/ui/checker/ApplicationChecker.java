package pg.ui.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.props.ApplicationPropertiesHelper;
import pg.util.StringUtils;

/**Created by Gawa 2017-10-07*/
public class ApplicationChecker implements Checker {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationChecker.class);

    private final ApplicationPropertiesHelper loader;

    public ApplicationChecker() {
        loader = ApplicationPropertiesHelper.getInstance();
    }

    @Override
    public boolean ready() {
        final String BASIC_MESSAGE = "Basic configuration incomplete.";
        if (StringUtils.nullOrTrimEmpty(loader.getUsername())) {
            logger.info("{} Login not found.", BASIC_MESSAGE);
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getPassword())) {
            logger.info("{} Password not found.", BASIC_MESSAGE);
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getServerUrl())) {
            logger.info("{} Server url not found.", BASIC_MESSAGE);
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getCreationMethod(""))) {
            logger.info("{} Task creation method not found.", BASIC_MESSAGE);
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getTorrentUrlType(""))) {
            logger.info("{} Torrent url type not found.", BASIC_MESSAGE);
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getDestination())) {
            logger.info("{} Synology download folder not found.", BASIC_MESSAGE);
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getApiInfo())) {
            logger.info("{} Synology API Info not found.", BASIC_MESSAGE);
            return false;
        }
        if (StringUtils.nullOrTrimEmpty(loader.getFilePath(""))) {
            logger.info("{} Store location for json files not found.", BASIC_MESSAGE);
            return false;
        }
        return true;
    }
}
