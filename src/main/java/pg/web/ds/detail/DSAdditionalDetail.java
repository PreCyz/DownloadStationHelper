package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Created by Gawa 2018-03-03 */
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

    public long getCompletedTime() {
        return completedTime;
    }

    public long getConnectedLeechers() {
        return connectedLeechers;
    }

    public long getConnectedPeers() {
        return connectedPeers;
    }

    public long getConnectedSeeders() {
        return connectedSeeders;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getDestination() {
        return destination;
    }

    public String getPriority() {
        return priority;
    }

    public long getSeedElapsed() {
        return seedElapsed;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public long getTotalPeers() {
        return totalPeers;
    }

    public long getTotalPieces() {
        return totalPieces;
    }

    public String getUnzipPassword() {
        return unzipPassword;
    }

    public String getUri() {
        return uri;
    }

    public long getWaitingSeconds() {
        return waitingSeconds;
    }
}
