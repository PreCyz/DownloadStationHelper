package pg.ui.window.controller.completable;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DsApiDetail;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ResumeTaskCompletable extends ManageTaskCompletable {

    public ResumeTaskCompletable(Property<ObservableList<TaskDetail>> itemProperty, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                                 List<TaskDetail> torrentsToResume, CheckBox liveTrackCheckbox, ExecutorService executor) {
        super(itemProperty, dsApiDetail, windowHandler, torrentsToResume, liveTrackCheckbox, executor);
    }

    @Override
    protected DSTaskMethod getTaskMethod() {
        return DSTaskMethod.RESUME;
    }
}
