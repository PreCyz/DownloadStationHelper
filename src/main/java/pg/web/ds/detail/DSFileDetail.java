package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Created by Gawa 2018-03-03 */
public class DSFileDetail {

    private String filename;
    private long index;
    private String priority;
    private String size;
    @JsonProperty("size_downloaded")
    private String sizeDownloaded;
    private boolean wanted;

    public String getFilename() {
        return filename;
    }

    public long getIndex() {
        return index;
    }

    public String getPriority() {
        return priority;
    }

    public String getSize() {
        return size;
    }

    public String getSizeDownloaded() {
        return sizeDownloaded;
    }

    public boolean isWanted() {
        return wanted;
    }
}
