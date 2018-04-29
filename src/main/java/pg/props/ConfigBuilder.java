package pg.props;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.program.TorrentUrlType;
import pg.util.StringUtils;
import pg.web.ds.DSAllowedProtocol;
import pg.web.ds.detail.DSMethod;

import java.util.Properties;

import static pg.program.SettingKeys.*;

/**Created by Gawa 2017-10-15*/
public class ConfigBuilder {

    private static final Logger logger = LogManager.getLogger(ConfigBuilder.class);

    private final Properties config;

    public ConfigBuilder() {
        this.config = new Properties();
    }

    public ConfigBuilder withServerUrl(String serverUrl) {
        logger.info("Server url set [{}].", serverUrl);
        if (StringUtils.nullOrTrimEmpty(serverUrl)) {
            return this;
        }
        config.setProperty(SERVER_URL.key(), serverUrl);
        return this;
    }

    public ConfigBuilder withServerPort(String serverPort) {
        logger.info("Server port set [{}].", serverPort);
        if (StringUtils.nullOrTrimEmpty(serverPort)) {
            return this;
        }
        String port = String.valueOf(DSAllowedProtocol.https.port());
        if (serverPort.endsWith(DSAllowedProtocol.http.name())) {
            port = String.valueOf(DSAllowedProtocol.http.port());
        }
        config.setProperty(SERVER_PORT.key(), port);
        return this;
    }

    public ConfigBuilder withLogin(String login) {
        logger.info("Server login set [{}].", login);
        if (StringUtils.nullOrTrimEmpty(login)) {
            return this;
        }
        config.setProperty(USERNAME.key(), login);
        return this;
    }

    public ConfigBuilder withPassword(String password) {
        logger.info("Server password set [{}].", password);
        if (StringUtils.nullOrTrimEmpty(password)) {
            return this;
        }
        config.setProperty(PASSWORD.key(), password);
        return this;
    }

    public ConfigBuilder withDownloadTo(String downloadTo) {
        logger.info("Server download location set [{}].", downloadTo);
        if (StringUtils.nullOrTrimEmpty(downloadTo)) {
            return this;
        }
        config.setProperty(DESTINATION.key(), downloadTo);
        return this;
    }

    public ConfigBuilder withApiUrl(String apiUrl) {
        logger.info("Torrent api url set [{}].", apiUrl);
        if (StringUtils.nullOrTrimEmpty(apiUrl)) {
            return this;
        }
        config.setProperty(URL.key(), apiUrl);
        return this;
    }

    public ConfigBuilder withQueryLimit(String queryLimit) {
        logger.info("Query limit set [{}].", queryLimit);
        if (StringUtils.nullOrTrimEmpty(queryLimit)) {
            return this;
        }
        config.setProperty(LIMIT.key(), queryLimit);
        return this;
    }

    public ConfigBuilder withQueryPage(String queryPage) {
        logger.info("Query page set [{}].", queryPage);
        if (StringUtils.nullOrTrimEmpty(queryPage)) {
            return this;
        }
        config.setProperty(PAGE.key(), queryPage);
        return this;
    }

    public ConfigBuilder withTorrentAge(String torrentAge) {
        logger.info("Filter torrent age set [{}].", torrentAge);
        if (StringUtils.nullOrTrimEmpty(torrentAge)) {
            return this;
        }
        config.setProperty(TORRENT_AGE_DAYS.key(), torrentAge);
        return this;
    }

    public ConfigBuilder withMaxFileSize(String maxFileSize) {
        logger.info("Filter max file size set [{}].", maxFileSize);
        if (StringUtils.nullOrTrimEmpty(maxFileSize)) {
            return this;
        }
        config.setProperty(MAX_FILE_SIZE.key(), maxFileSize);
        return this;
    }

    public ConfigBuilder withReleaseDate(String releaseDate) {
        logger.info("Filter release date set [{}].", releaseDate);
        if (StringUtils.nullOrTrimEmpty(releaseDate)) {
            return this;
        }
        config.setProperty(TORRENT_RELEASE_DATE.key(), releaseDate);
        return this;
    }

    public ConfigBuilder withRepeatDownload(boolean repeatDownload) {
        logger.info("Filter repeat download set [{}].", repeatDownload);
        config.setProperty(REPEAT_DOWNLOAD.key(), StringUtils.stringFromBoolean(repeatDownload));
        return this;
    }

    public ConfigBuilder withHandleDuplicates(boolean handleDuplicates) {
        logger.info("Filter repeat download set [{}].", handleDuplicates);
        config.setProperty(HANDLE_DUPLICATES.key(), StringUtils.stringFromBoolean(handleDuplicates));
        return this;
    }

    public ConfigBuilder withTorrentLocation(String torrentLocation) {
        logger.info("Torrent location where to save *.torrent files set [{}].", torrentLocation);
        if (StringUtils.nullOrTrimEmpty(torrentLocation)) {
            return this;
        }
        config.setProperty(TORRENT_LOCATION.key(), torrentLocation);
        return this;
    }

    public ConfigBuilder withResultLocation(String resultLocation) {
        logger.info("Location with result set [{}].", resultLocation);
        if (StringUtils.nullOrTrimEmpty(resultLocation)) {
            return this;
        }
        config.setProperty(RESULT_LOCATION.key(), resultLocation);
        return this;
    }

    public ConfigBuilder withCreationMethod(DSMethod creationMethod) {
        logger.info("Creation method set [{}].", creationMethod.name());
        config.setProperty(CREATION_METHOD.key(), creationMethod.name());
        return this;
    }

    public ConfigBuilder withTorrentUrlType(TorrentUrlType torrentUrlType) {
        logger.info("Torrent url type set [{}]", torrentUrlType.name());
        config.setProperty(TORRENT_URL_TYPE.key(), torrentUrlType.name());
        return this;
    }

    public ConfigBuilder withApiInfo(String apiInfo) {
        if (StringUtils.nullOrTrimEmpty(apiInfo)) {
            apiInfo = "/webapi/query.cgi?api=SYNO.API.Info&version=1&method=query&query=SYNO.API.Auth,SYNO.DownloadStation.Task";
        }
        logger.info("Synology Api Info set [{}]", apiInfo);
        config.setProperty(API_INFO.key(), apiInfo);
        return this;
    }

    public ConfigBuilder withLiveTrackInterval(String liveTrackInterval) {
        if (StringUtils.nullOrTrimEmpty(liveTrackInterval)) {
            liveTrackInterval = "0";
        }
        logger.info("Live track interval [{}]", liveTrackInterval);
        config.setProperty(LIVE_TRACK.key(), liveTrackInterval);
        return this;
    }

    public Properties createConfig() {
        return config;
    }
}
