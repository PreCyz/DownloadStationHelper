package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/** Created by Gawa 2018-03-03 */
@Getter
public class DSAdditionalDetail {

    @JsonProperty("completed_time")
    private long completedTime;
    @JsonProperty("connected_leechers")
    private long connectedLeechers;
    @JsonProperty("connected_peers")
    private long connectedPeers;
    @JsonProperty("connected_seeders")
    private long connectedSeeders;
    @JsonProperty("create_time")
    private String createTime;
    private String destination;
    private String priority;
    @JsonProperty("seedelapsed")
    private long seedElapsed;
    @JsonProperty("started_time")
    private long startedTime;
    @JsonProperty("total_peers")
    private long totalPeers;
    @JsonProperty("total_pieces")
    private long totalPieces;
    @JsonProperty("unzip_password")
    private String unzipPassword;
    private String uri;
    @JsonProperty("waiting_seconds")
    private long waitingSeconds;

}
