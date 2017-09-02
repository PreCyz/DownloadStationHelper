package pg.service;

import pg.web.model.ApiName;
import pg.web.model.SettingKeys;
import pg.web.synology.AuthMethod;
import pg.web.synology.DSTaskMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**Created by Gawa on 15/08/17*/
public class SidExecutor extends AbstractExecutor {

    public SidExecutor(Properties shows, Properties application) {
        super(shows, application);
    }

    @Override
    protected String buildLoginUrl(String serverUrl) {
        String userName = application.getProperty(SettingKeys.USERNAME.key());
        String password = application.getProperty(SettingKeys.PASSWORD.key());
        return serverUrl + "/webapi/" + authInfo.getPath() +
                "?" +
                "api=" + ApiName.API_AUTH + "&" +
                "version=" + authInfo.getMaxVersion() + "&" +
                "method=" + AuthMethod.LOGIN.method() + "&" +
                "account=" + userName + "&" +
                "passwd=" + password + "&" +
                "session=" + "DownloadStation" + "&" +
                "format=" + "sid";
    }

    @Override
    protected String buildCreateTaskUrl(String serverUrl) {
        final String urlType = application.getProperty(SettingKeys.TORRENT_URL_TYPE.key(), "torrent");
        String uri = String.join(",", foundTorrents.stream()
                .map(reducedDetail -> {
                    if ("magnet".equals(urlType)) {
                        return reducedDetail.getMagnetUrl();
                    }
                    return reducedDetail.getTorrentUrl();
                })
                .collect(Collectors.toList()));
        String destination = application.getProperty(SettingKeys.DESTINATION.key());

        return serverUrl + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + DSTaskMethod.CREATE.method() + "&" +
                "_sid=" + sid + "&" +
                "destination=" + destination + "&" +
                "uri=" + uri;
    }

    @Override
    protected String buildTaskListUrl(String serverUrl) {
        return serverUrl + "/webapi/" + downloadStationTask.getPath() +
                        "?" +
                        "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                        "version=" + downloadStationTask.getMaxVersion() + "&" +
                        "method=" + "list" + "&" +
                        "_sid=" + sid;
    }

    @Override
    protected String buildLogoutUrl(String serverUrl) {
        return serverUrl + "/webapi/" + authInfo.getPath() +
                "?" +
                "api=" + ApiName.API_AUTH + "&" +
                "version=" + authInfo.getMaxVersion() + "&" +
                "method=" + AuthMethod.LOGOUT.method() + "&" +
                "session=" + "DownloadStation" + "&" +
                "format=" + "sid";
    }

    @Override
    protected Map<String, String> createCookieMap() {
        return new HashMap<>();
    }
}
