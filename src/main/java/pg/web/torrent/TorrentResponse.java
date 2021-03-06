package pg.web.torrent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**Created by Gawa on 15/08/17.*/
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TorrentResponse {
    @JsonProperty("imdb_id")
    private String imdbId;
    @JsonProperty("torrents_count")
    private int torrentsCount;
    private int limit;
    private int page;
    private List<TorrentDetail> torrents;

    @Override
    public String toString() {
        return "TorrentResponse{" +
                "torrentsCount=" + torrentsCount +
                ", limit=" + limit +
                ", page=" + page +
                ", torrentsSize=" + torrents.size() +
                '}';
    }
}
