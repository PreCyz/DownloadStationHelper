package pg.web.ds;

/**Created by Gawa on 28/08/17.*/
public enum DSTaskDownloadStatus {
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
