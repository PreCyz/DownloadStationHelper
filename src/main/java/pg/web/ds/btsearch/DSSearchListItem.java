package pg.web.ds.btsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import lombok.Getter;

@Getter
public class DSSearchListItem {
    private String date;
    @JsonProperty("download_uri")
    private String downloadUri;
    @JsonProperty("external_link")
    private String externalLink;
    private int id;
    private int leechs;
    @JsonProperty("module_id")
    private String moduleId;
    @JsonProperty("module_title")
    private String moduleTitle;
    private int peers;
    private int seeds;
    private String size;
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DSSearchListItem that = (DSSearchListItem) o;
        return getId() == that.getId() &&
                getLeechs() == that.getLeechs() &&
                getPeers() == that.getPeers() &&
                getSeeds() == that.getSeeds() &&
                Objects.equal(getDate(), that.getDate()) &&
                Objects.equal(getDownloadUri(), that.getDownloadUri()) &&
                Objects.equal(getExternalLink(), that.getExternalLink()) &&
                Objects.equal(getModuleId(), that.getModuleId()) &&
                Objects.equal(getModuleTitle(), that.getModuleTitle()) &&
                Objects.equal(getSize(), that.getSize()) &&
                Objects.equal(getTitle(), that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDate(), getDownloadUri(), getExternalLink(), getId(), getLeechs(), getModuleId(),
                getModuleTitle(), getPeers(), getSeeds(), getSize(), getTitle());
    }

    @Override
    public String toString() {
        return "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", peers=" + peers +
                ", seeds=" + seeds +
                ", size='" + size + '\'' +
                '}';
    }
}
