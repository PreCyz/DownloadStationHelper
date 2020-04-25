package pg.web.torrent;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pg.program.SearchItem;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**Created by Gawa on 15/08/17.*/
@NoArgsConstructor
@AllArgsConstructor
public class ReducedDetail implements Duplicable {

    private String title;
    @Getter
    private int matchPrecision;
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date dateReleased;
    @Getter
    private String magnetUrl;
    @Getter
    private String torrentUrl;
    @Getter
    private String imdbId;
    private String season;
    private String episode;

    public final static ReducedDetail NOTHING_TO_DISPLAY = new ReducedDetail("Nothing to display");

    private ReducedDetail(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSeason() {
        return season;
    }

    @Override
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

    public static ReducedDetail valueFrom(SearchItem searchItem) {
        ReducedDetail reducedDetail = new ReducedDetail(searchItem.getTitle());
        reducedDetail.magnetUrl = searchItem.getDownloadUri();
        reducedDetail.torrentUrl = searchItem.getDownloadUri();
        reducedDetail.dateReleased = Date.from(
                LocalDate.parse(searchItem.getDate(), DateTimeFormatter.ISO_DATE)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
        return reducedDetail;
    }
}
