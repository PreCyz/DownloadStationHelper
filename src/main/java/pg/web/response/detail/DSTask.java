package pg.web.response.detail;

import pg.web.model.DownloadStatus;

/**Created by Gawa on 27/08/17.*/
public class DSTask {
    private String id;
    private long size;
    private DownloadStatus status;
    private String title;
    private String type;
    private String username;

    public String getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public DownloadStatus getStatus() {
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
}
