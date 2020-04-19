package pg.program;

import com.google.common.base.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pg.web.ds.btsearch.DSSearchListItem;

/** Created by Gawa 2018-03-03 */
public class SearchItem {
    private LongProperty id = new SimpleLongProperty(this, "id", 0);
    private LongProperty seeds = new SimpleLongProperty(this, "seeds", 0);
    private LongProperty peers = new SimpleLongProperty(this, "peers", 0);
    private LongProperty size = new SimpleLongProperty(this, "size", 0);
    private LongProperty leechs = new SimpleLongProperty(this, "leechs", 0);
    private StringProperty title = new SimpleStringProperty(this, "title", "");
    private StringProperty downloadUri = new SimpleStringProperty(this, "downloadUri", "");
    private StringProperty externalLink = new SimpleStringProperty(this, "externalLink", "");
    private StringProperty date = new SimpleStringProperty(this, "date", "");
    private StringProperty moduleId = new SimpleStringProperty(this, "moduleId", "");
    private StringProperty moduleTitle = new SimpleStringProperty(this, "moduleTitle", "");


    public SearchItem() {}

    private SearchItem(Long id, Long seeds, Long peers, Long size, Long leechs, String title, String downloadUri,
                       String externalLink, String date, String moduleId, String moduleTitle) {
        this.id = new SimpleLongProperty(this, "id", id);
        this.leechs = new SimpleLongProperty(this, "leechs", leechs);
        this.seeds = new SimpleLongProperty(this, "seeds", seeds);
        this.peers = new SimpleLongProperty(this, "peers", peers);
        this.size = new SimpleLongProperty(this, "size", size);
        this.date = new SimpleStringProperty(this, "date", date);
        this.title = new SimpleStringProperty(this, "title", title);
        this.downloadUri = new SimpleStringProperty(this, "downloadUri", downloadUri);
        this.externalLink = new SimpleStringProperty(this, "externalLink", externalLink);
        this.moduleId = new SimpleStringProperty(this, "moduleId", moduleId);
        this.moduleTitle = new SimpleStringProperty(this, "moduleTitle", moduleTitle);
    }

    public static SearchItem getNothingToDisplay() {
        return new SearchItem(0L, 0L, 0L, 0L, 0L, "Nothing to display", "", "", "", "", "");
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public long getSeeds() {
        return seeds.get();
    }

    public LongProperty seedsProperty() {
        return seeds;
    }

    public void setSeeds(long seeds) {
        this.seeds.set(seeds);
    }

    public long getPeers() {
        return peers.get();
    }

    public LongProperty peersProperty() {
        return peers;
    }

    public void setPeers(long peers) {
        this.peers.set(peers);
    }

    public long getSize() {
        return size.get();
    }

    public LongProperty sizeProperty() {
        return size;
    }

    public void setSize(long size) {
        this.size.set(size);
    }

    public long getLeechs() {
        return leechs.get();
    }

    public LongProperty leechsProperty() {
        return leechs;
    }

    public void setLeechs(long leechs) {
        this.leechs.set(leechs);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDownloadUri() {
        return downloadUri.get();
    }

    public StringProperty downloadUriProperty() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri.set(downloadUri);
    }

    public String getExternalLink() {
        return externalLink.get();
    }

    public StringProperty externalLinkProperty() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink.set(externalLink);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getModuleId() {
        return moduleId.get();
    }

    public StringProperty moduleIdProperty() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId.set(moduleId);
    }

    public String getModuleTitle() {
        return moduleTitle.get();
    }

    public StringProperty moduleTitleProperty() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle.set(moduleTitle);
    }

    public static SearchItem valueFrom(DSSearchListItem item) {
        return new SearchItem(
                (long)item.getId(),
                (long)item.getSeeds(),
                (long)item.getPeers(),
                Long.parseLong(item.getSize()),
                (long)item.getLeechs(),
                item.getTitle(),
                item.getDownloadUri(),
                item.getExternalLink(),
                item.getDate(),
                item.getModuleId(),
                item.getModuleTitle()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchItem that = (SearchItem) o;
        return Objects.equal(getId(), that.getId()) &&
                Objects.equal(getSeeds(), that.getSeeds()) &&
                Objects.equal(getPeers(), that.getPeers()) &&
                Objects.equal(getSize(), that.getSize()) &&
                Objects.equal(getLeechs(), that.getLeechs()) &&
                Objects.equal(getTitle(), that.getTitle()) &&
                Objects.equal(getDownloadUri(), that.getDownloadUri()) &&
                Objects.equal(getExternalLink(), that.getExternalLink()) &&
                Objects.equal(getDate(), that.getDate()) &&
                Objects.equal(getModuleId(), that.getModuleId()) &&
                Objects.equal(getModuleTitle(), that.getModuleTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getSeeds(), getPeers(), getSize(), getLeechs(), getTitle(), getDownloadUri(),
                getExternalLink(), getDate(), getModuleId(), getModuleTitle());
    }

    @Override
    public String toString() {
        return "SearchItem{" +
                "id=" + id +
                ", seeds=" + seeds +
                ", peers=" + peers +
                ", size=" + size +
                ", leechs=" + leechs +
                ", title=" + title +
                ", downloadUri=" + downloadUri +
                ", externalLink=" + externalLink +
                ", date=" + date +
                ", moduleId=" + moduleId +
                ", moduleTitle=" + moduleTitle +
                '}';
    }
}
