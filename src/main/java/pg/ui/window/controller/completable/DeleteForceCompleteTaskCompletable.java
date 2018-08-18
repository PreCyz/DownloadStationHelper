package pg.ui.window.controller.completable;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DsApiDetail;

import java.util.List;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2017-10-29 */
public class DeleteForceCompleteTaskCompletable extends ManageTaskCompletable {

    public DeleteForceCompleteTaskCompletable(TableView<TaskDetail> tableView, DsApiDetail dsApiDetail,
                                              WindowHandler windowHandler, List<TaskDetail> torrentsToManage,
                                              CheckBox liveTrackCheckbox, ExecutorService executor) {
        super(tableView, dsApiDetail, windowHandler, torrentsToManage, liveTrackCheckbox, executor);
    }

    @Override
    protected DSTaskMethod getTaskMethod() {
        return DSTaskMethod.DELETE_FORCE;
    }

}
