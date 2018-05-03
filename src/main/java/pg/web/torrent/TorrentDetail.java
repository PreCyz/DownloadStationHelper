package pg.web.torrent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**Created by Gawa on 15/08/17*/
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TorrentDetail implements Duplicable {
    private int id;
    private String hash;
    private String filename;
    @JsonProperty("episode_url")
    private String episodeUrl;
    @JsonProperty("torrent_url")
    private String torrentUrl;
    @JsonProperty("magnet_url")
    private String magnetUrl;
    private String title;
    @JsonProperty("imdb_id")
    private String imdbId;
    private String season;
    private String episode;
    @JsonProperty("small_screenshot")
    private String smallScreenshot;
    @JsonProperty("large_screenshot")
    private String largeScreenshot;
    private int seeds;
    private int peers;
    @JsonProperty("date_released_unix")
    private long dateReleased;
    @JsonProperty("size_bytes")
    private String size;

    public int getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public String getFilename() {
        return filename;
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

    public String getImdbId() {
        return imdbId;
    }

    public String getSeason() {
        return season;
    }

    public String getEpisode() {
        return episode;
    }

    public String getSmallScreenshot() {
        return smallScreenshot;
    }

    public String getLargeScreenshot() {
        return largeScreenshot;
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
