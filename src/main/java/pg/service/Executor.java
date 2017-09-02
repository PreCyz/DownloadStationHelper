package pg.service;

/**Created by Gawa on 26/08/17.*/
public interface Executor {
    void findTorrents();
    void matchTorrents();
    void writeTorrentsToFile();
    void prepareAvailableOperations();
    void loginToDiskStation();
    void logoutFromDiskStation();
    void createDownloadStationTasks();
    void listOfTasks();
    void writeTorrentsOnDS();
}
