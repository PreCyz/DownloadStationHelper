package pg.web.model.torrent;

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

    public ReducedDetail(String title, String magnetUrl, Date dateReleased, int matchPrecision, String torrentUrl) {
        this.title = title;
        this.magnetUrl = magnetUrl;
        this.dateReleased = dateReleased;
        this.matchPrecision = matchPrecision;
        this.torrentUrl = torrentUrl;
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
}
