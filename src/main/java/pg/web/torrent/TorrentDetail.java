package pg.web.torrent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**Created by Gawa on 15/08/17*/
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TorrentDetail implements Duplicable {
    @Getter
    private int id;
    @Getter
    private String hash;
    @Getter
    private String filename;
    @Getter
    @JsonProperty("episode_url")
    private String episodeUrl;
    @Getter
    @JsonProperty("torrent_url")
    private String torrentUrl;
    @Getter
    @JsonProperty("magnet_url")
    private String magnetUrl;
    private String title;
    @Getter
    @JsonProperty("imdb_id")
    private String imdbId;
    private String season;
    private String episode;
    @Getter
    @JsonProperty("small_screenshot")
    private String smallScreenshot;
    @Getter
    @JsonProperty("large_screenshot")
    private String largeScreenshot;
    @Getter
    private int seeds;
    @Getter
    private int peers;
    @Getter
    @JsonProperty("date_released_unix")
    private long dateReleased;
    @Getter
    @JsonProperty("size_bytes")
    private String size;

    public String getTitle() {
        return title;
    }

    public String getSeason() {
        return season;
    }

    public String getEpisode() {
        return episode;
    }

    @Override
    public String toString() {
        return "title='" + title + '\'' +
                ", season='" + season + '\'' +
                ", episode='" + episode + '\'' +
                '}';
    }
}
