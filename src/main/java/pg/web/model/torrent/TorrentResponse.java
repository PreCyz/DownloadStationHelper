package pg.web.model.torrent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**Created by Gawa on 15/08/17.*/
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TorrentResponse {
    @JsonProperty("imdb_id")
    private String imdbId;
    @JsonProperty("torrents_count")
    private int torrentsCount;
    private int limit;
    private int page;
    private List<TorrentDetail> torrents;

    public String getImdbId() {
        return imdbId;
    }

    public int getTorrentsCount() {
        return torrentsCount;
    }

    public int getLimit() {
        return limit;
    }

    public int getPage() {
        return page;
    }

    public List<TorrentDetail> getTorrents() {
        return torrents;
    }

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
