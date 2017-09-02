package pg.web.model.torrent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**Created by Gawa on 15/08/17*/
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TorrentDetail {
    private int id;
    @JsonProperty("episode_url")
    private String episodeUrl;
    @JsonProperty("torrent_url")
    private String torrentUrl;
    @JsonProperty("magnet_url")
    private String magnetUrl;
    private String title;
    private String hash;
    private String filename;
    @JsonProperty("small_screenshot")
    private String smallScreenshot;
    @JsonProperty("large_screenshot")
    private String large_screenshot;
    private int seeds;
    private int peers;
    @JsonProperty("date_released_unix")
    private long dateReleased;
    @JsonProperty("size_bytes")
    private String size;

    public int getId() {
        return id;
    }

    public String getEpisodeUrl() {
        return episodeUrl;
    }

    public String getTorrentUrl() {
        return torrentUrl;
    }

    public String getMagnetUrl() {
        return magnetUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getHash() {
        return hash;
    }

    public String getFilename() {
        return filename;
    }

    public String getSmallScreenshot() {
        return smallScreenshot;
    }

    public String getLarge_screenshot() {
        return large_screenshot;
    }

    public int getSeeds() {
        return seeds;
    }

    public int getPeers() {
        return peers;
    }

    public long getDateReleased() {
        return dateReleased;
    }

    public String getSize() {
        return size;
    }
}
