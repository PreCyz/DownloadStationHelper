package pg.web.ds.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Created by Gawa 2018-03-03 */
public class DSAdditionalDetail {

    @JsonProperty("connected_leechers")
    private long connectedLeechers;
    @JsonProperty("connected_seeders")
    private long connectedSeeders;
    @JsonProperty("create_time")
    private String createTime;
    private String destination;
    private String priority;
    @JsonProperty("total_peers")
    private long totalPeers;
    private String uri;

    public long getConnectedLeechers() {
        return connectedLeechers;
    }

    public void setConnectedLeechers(long connectedLeechers) {
        this.connectedLeechers = connectedLeechers;
    }

    public long getConnectedSeeders() {
        return connectedSeeders;
    }

    public void setConnectedSeeders(long connectedSeeders) {
        this.connectedSeeders = connectedSeeders;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public long getTotalPeers() {
        return totalPeers;
    }

    public void setTotalPeers(long totalPeers) {
        this.totalPeers = totalPeers;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
