package pg.web.model;

/**Created by Gawa on 28/08/17.*/
public enum DownloadStatus {
    waiting,
    downloading,
    paused,
    finishing,
    finished,
    hash_checking,
    seeding,
    filehosting_waiting,
    extracting,
    error
}
