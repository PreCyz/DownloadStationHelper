package pg.web.model;

/**Created by Gawa on 15/08/17.*/
public enum SettingKeys {
    URL("restUrl"),
    LIMIT("query.limit"),
    PAGE("query.page"),
    FILE_PATH("result.filePath"),
    USERNAME("synology.http.username"),
    PASSWORD("synology.http.password"),
    SERVER_URL("synology.server.url"),
    SERVER_PORT("synology.server.port"),
    API_INFO("synology.api.info"),
    DESTINATION("synology.download.folder"),
    WRITE_TO_FILE("write.to.file"),
    TORRENT_URL_TYPE("torrent.url.type"),
    CREATION_METHOD("task.creation.method"),
    TORRENT_LOCATION("torrent.file.location");

    private String key;

    SettingKeys(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
