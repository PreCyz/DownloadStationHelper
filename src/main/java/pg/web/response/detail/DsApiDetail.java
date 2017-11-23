package pg.web.response.detail;

import pg.web.model.ApiDetails;

/** Created by Gawa 2017-11-11 */
public class DsApiDetail {
    private ApiDetails authInfo;
    private ApiDetails downloadStationTask;
    private String sid;

    public ApiDetails getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(ApiDetails authInfo) {
        this.authInfo = authInfo;
    }

    public ApiDetails getDownloadStationTask() {
        return downloadStationTask;
    }

    public void setDownloadStationTask(ApiDetails downloadStationTask) {
        this.downloadStationTask = downloadStationTask;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
