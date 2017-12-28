package pg.web.torrent;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**Created by Gawa on 15/08/17.*/
public class ReducedDetail {

    private String title;
    private int matchPrecision;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date dateReleased;
    private String magnetUrl;
    private String torrentUrl;
    private String imdbId;
    private String season;
    private String episode;

    public final static ReducedDetail NOTHING_TO_DISPLAY = new ReducedDetail("Nothing to display");

    public ReducedDetail() {}

    private ReducedDetail(String title) {
        this.title = title;
    }

    ReducedDetail(String title, int matchPrecision, Date dateReleased, String magnetUrl, String torrentUrl,
                         String imdbId, String season, String episode) {
        this(title);
        this.matchPrecision = matchPrecision;
        this.dateReleased = dateReleased;
        this.magnetUrl = magnetUrl;
        this.torrentUrl = torrentUrl;
        this.imdbId = imdbId;
        this.season = season;
        this.episode = episode;
    }

    public String getTitle() {
        return title;
    }

    public String getMagnetUrl() {
        return magnetUrl;
    }

    public Date getDateReleased() {
        return dateReleased;
    }

    public int getMatchPrecision() {
        return matchPrecision;
    }

    public String getTorrentUrl() {
        return torrentUrl;
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

    @Override
    public String toString() {
        return title;
    }
}
