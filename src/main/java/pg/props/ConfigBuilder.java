package pg.props;

import pg.util.StringUtils;
import pg.web.model.AllowedPorts;
import pg.web.model.DSMethod;
import pg.web.model.TorrentUrlType;

import java.util.Properties;

import static pg.web.model.SettingKeys.*;

/**Created by Gawa 2017-10-15*/
public class ConfigBuilder {
    private final Properties config;

    public ConfigBuilder() {
        this.config = new Properties();
    }

    public ConfigBuilder withServerUrl(String serverUrl) {
        config.setProperty(SERVER_URL.key(), serverUrl);
        return this;
    }

    public ConfigBuilder withServerPort(String serverPort) {
        if (StringUtils.nullOrTrimEmpty(serverPort)) {
            return this;
        }
        String port = String.valueOf(AllowedPorts.HTTPS.port());
        if (serverPort.endsWith(AllowedPorts.HTTP.name())) {
            port = String.valueOf(AllowedPorts.HTTP.port());
        }
        config.setProperty(SERVER_PORT.key(), port);
        return this;
    }

    public ConfigBuilder withLogin(String login) {
        if (StringUtils.nullOrTrimEmpty(login)) {
            return this;
        }
        config.setProperty(USERNAME.key(), login);
        return this;
    }

    public ConfigBuilder withPassword(String password) {
        if (StringUtils.nullOrTrimEmpty(password)) {
            return this;
        }
        config.setProperty(PASSWORD.key(), password);
        return this;
    }

    public ConfigBuilder withDownloadTo(String downloadTo) {
        if (StringUtils.nullOrTrimEmpty(downloadTo)) {
            return this;
        }
        config.setProperty(DESTINATION.key(), downloadTo);
        return this;
    }

    public ConfigBuilder withApiUrl(String apiUrl) {
        if (StringUtils.nullOrTrimEmpty(apiUrl)) {
            return this;
        }
        config.setProperty(URL.key(), apiUrl);
        return this;
    }

    public ConfigBuilder withQueryLimit(String queryLimit) {
        if (StringUtils.nullOrTrimEmpty(queryLimit)) {
            return this;
        }
        config.setProperty(LIMIT.key(), queryLimit);
        return this;
    }

    public ConfigBuilder withQueryPage(String queryPage) {
        if (StringUtils.nullOrTrimEmpty(queryPage)) {
            return this;
        }
        config.setProperty(PAGE.key(), queryPage);
        return this;
    }

    public ConfigBuilder withTorrentAge(String torrentAge) {
        if (StringUtils.nullOrTrimEmpty(torrentAge)) {
            return this;
        }
        config.setProperty(TORRENT_AGE_DAYS.key(), torrentAge);
        return this;
    }

    public ConfigBuilder withMaxFileSize(String maxFileSize) {
        if (StringUtils.nullOrTrimEmpty(maxFileSize)) {
            return this;
        }
        config.setProperty(MAX_FILE_SIZE.key(), maxFileSize);
        return this;
    }

    public ConfigBuilder withReleaseDate(String releaseDate) {
        if (StringUtils.nullOrTrimEmpty(releaseDate)) {
            return this;
        }
        config.setProperty(TORRENT_RELEASE_DATE.key(), releaseDate);
        return this;
    }

    public ConfigBuilder withRepeatDownload(boolean repeatDownload) {
        config.setProperty(REPEAT_DOWNLOAD.key(), StringUtils.stringFromBoolean(repeatDownload));
        return this;
    }

    public ConfigBuilder withTorrentLocation(String torrentLocation) {
        if (StringUtils.nullOrTrimEmpty(torrentLocation)) {
            return this;
        }
        config.setProperty(TORRENT_LOCATION.key(), torrentLocation);
        return this;
    }

    public ConfigBuilder withResultLocation(String resultLocation) {
        if (StringUtils.nullOrTrimEmpty(resultLocation)) {
            return this;
        }
        config.setProperty(RESULT_LOCATION.key(), resultLocation);
        return this;
    }

    public ConfigBuilder withCreationMethod(DSMethod creationMethod) {
        config.setProperty(CREATION_METHOD.key(), creationMethod.name());
        return this;
    }

    public ConfigBuilder withTorrentUrlType(TorrentUrlType torrentUrlType) {
        config.setProperty(TORRENT_URL_TYPE.key(), torrentUrlType.name());
        return this;
    }

    public ConfigBuilder withApiInfo(String apiInfo) {
        if (StringUtils.nullOrTrimEmpty(apiInfo)) {
            apiInfo = "/webapi/query.cgi?api=SYNO.API.Info&version=1&method=query&query=SYNO.API.Auth,SYNO.DownloadStation.Task";
        }
        config.setProperty(API_INFO.key(), apiInfo);
        return this;
    }

    public Properties createConfig() {
        return config;
    }
}
