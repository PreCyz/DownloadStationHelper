package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Created by Gawa 2018-03-03 */
public class DSFileDetail {

    private String filename;
    private String priority;
    private String size;
    @JsonProperty("size_downloaded")
    private String sizeDownloaded;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSizeDownloaded() {
        return sizeDownloaded;
    }

    public void setSizeDownloaded(String sizeDownloaded) {
        this.sizeDownloaded = sizeDownloaded;
    }
}
