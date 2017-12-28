package pg.web.response.detail;

/** Created by Gawa 2017-11-11 */
public class DsApiDetail {
    private DSApiDetails authInfo;
    private DSApiDetails downloadStationTask;
    private String sid;

    public DSApiDetails getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(DSApiDetails authInfo) {
        this.authInfo = authInfo;
    }

    public DSApiDetails getDownloadStationTask() {
        return downloadStationTask;
    }

    public void setDownloadStationTask(DSApiDetails downloadStationTask) {
        this.downloadStationTask = downloadStationTask;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
