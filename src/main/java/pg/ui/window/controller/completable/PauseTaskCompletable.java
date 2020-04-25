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

public class PauseTaskCompletable extends ManageTaskCompletable {

    public PauseTaskCompletable(Property<ObservableList<TaskDetail>> itemProperty, DsApiDetail dsApiDetail, WindowHandler windowHandler, List<TaskDetail> torrentsToManage, CheckBox liveTrackCheckbox, ExecutorService executor) {
        super(itemProperty, dsApiDetail, windowHandler, torrentsToManage, liveTrackCheckbox, executor);
    }

    @Override
    protected DSTaskMethod getTaskMethod() {
        return DSTaskMethod.PAUSE;
    }
}
