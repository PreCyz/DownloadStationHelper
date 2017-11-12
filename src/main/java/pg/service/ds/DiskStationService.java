package pg.service.ds;

/**Created by Gawa 2017-09-15*/
public interface DiskStationService {
    void prepareAvailableOperations();
    void loginToDiskStation();
    void createDownloadStationTasks();
    void listOfTasks();
    void logoutFromDiskStation();
    void writeTorrentsOnDS();
}
