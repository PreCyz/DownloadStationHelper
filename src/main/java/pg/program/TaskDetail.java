package pg.program;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Created by Gawa 2018-03-03 */
public class TaskDetail {
    private StringProperty id = new SimpleStringProperty(this, "id", "");
    private StringProperty title = new SimpleStringProperty(this, "title", "");
    private StringProperty status = new SimpleStringProperty(this, "status", "");
    private DoubleProperty progress = new SimpleDoubleProperty(this, "progress", 0);

    public TaskDetail() {}

    private TaskDetail(String title) {
        this.id = new SimpleStringProperty(this, "id", "");
        this.title = new SimpleStringProperty(this, "title", title);
        this.status = new SimpleStringProperty(this, "status", "");
        this.progress = new SimpleDoubleProperty(this, "progress", Double.NaN);
    }

    public static TaskDetail getNothingToDisplay() {
        return new TaskDetail("Nothing to display");
    }

    public boolean isNothingToDisplay() {
        return getTitle().equals(getNothingToDisplay().getTitle());
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
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

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    @Override
    public String toString() {
        return "TaskDetail{" +
                "id=" + id +
                ", title=" + title +
                ", status=" + status +
                ", progress=" + progress +
                '}';
    }
}
