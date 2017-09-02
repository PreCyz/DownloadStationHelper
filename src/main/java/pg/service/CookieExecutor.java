package pg.service;

import pg.web.model.ApiName;
import pg.web.model.SettingKeys;
import pg.web.model.torrent.ReducedDetail;
import pg.web.synology.AuthMethod;
import pg.web.synology.DSTaskMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**Created by Gawa on 15/08/17*/
public class CookieExecutor extends AbstractExecutor {

    public CookieExecutor(Properties shows, Properties application) {
        super(shows, application);
    }

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
                "format=" + "cookie";
    }

    @Override
    protected String buildCreateTaskUrl(String serverUrl) {
        String uri = String.join(",", foundTorrents.stream()
                //.map(ReducedDetail::getMagnetUrl)
                .map(ReducedDetail::getTorrentUrl)
                .collect(Collectors.toList()));
        String destination = application.getProperty(SettingKeys.DESTINATION.key());

        return serverUrl + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + DSTaskMethod.CREATE.method() + "&" +
                "destination=" + destination + "&" +
                "uri=" + uri;
    }

    @Override
    protected Map<String, String> createCookieMap() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("id", sid);
        return cookies;
    }

    @Override
    protected String buildTaskListUrl(String serverUrl) {
        return serverUrl + "/webapi/" + downloadStationTask.getPath() +
                "?" +
                "api=" + ApiName.DOWNLOAD_STATION_TASK + "&" +
                "version=" + downloadStationTask.getMaxVersion() + "&" +
                "method=" + "list";
    }

    @Override
    protected String buildLogoutUrl(String serverUrl) {
        return serverUrl + "/webapi/" + authInfo.getPath() +
                "?" +
                "api=" + ApiName.API_AUTH + "&" +
                "version=" + authInfo.getMaxVersion() + "&" +
                "method=" + AuthMethod.LOGOUT.method() + "&" +
                "session=" + "DownloadStation";
    }
}
