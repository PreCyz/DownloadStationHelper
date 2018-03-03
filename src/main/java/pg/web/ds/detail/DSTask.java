package pg.web.ds.detail;

import pg.web.ds.DSTaskDownloadStatus;

/**Created by Gawa on 27/08/17.*/
public class DSTask {
    private String id;
    private long size;
    private DSTaskDownloadStatus status;
    private String title;
    private String type;
    private String username;
    private DSAdditional additional;

    public DSTask() {}

    private DSTask(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public DSTaskDownloadStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public DSAdditional getAdditional() {
        return additional;
    }
}
