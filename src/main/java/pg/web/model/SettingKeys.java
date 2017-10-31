package pg.web.model;

/**Created by Gawa on 15/08/17.*/
public enum SettingKeys {
    URL("eztv.restUrl"),
    LIMIT("query.limit"),
    PAGE("query.page"),
    RESULT_LOCATION("result.filePath"),
    USERNAME("synology.http.username"),
    PASSWORD("synology.http.password"),
    SERVER_URL("synology.server.url"),
    SERVER_PORT("synology.server.port"),
    API_INFO("synology.api.info"),
    DESTINATION("synology.download.folder"),
    TORRENT_URL_TYPE("torrent.url.type"),
    CREATION_METHOD("task.creation.method"),
    TORRENT_LOCATION("torrent.file.location"),
    TORRENT_AGE_DAYS("torrent.age.days"),
    TORRENT_RELEASE_DATE("torrent.release.date"),
    REPEAT_DOWNLOAD("download.torrent.again"),
    HANDLE_DUPLICATES("handle.duplicates"),
    MAX_FILE_SIZE("max.file.size");

    private String key;

    SettingKeys(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
