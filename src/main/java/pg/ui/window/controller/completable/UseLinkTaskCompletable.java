package pg.ui.window.controller.completable;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import pg.program.TaskDetail;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactory;
import pg.ui.window.controller.task.atomic.call.ds.ManageTaskFactoryBean;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DsApiDetail;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2018-02-10 */
public class UseLinkTaskCompletable extends ListTaskCompletable {

    private String link;

    public UseLinkTaskCompletable(Property<ObservableList<TaskDetail>> itemProperty, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                                  String link, CheckBox liveTrackCheckbox, ExecutorService executor) {
        super(itemProperty, dsApiDetail, windowHandler, liveTrackCheckbox, executor);
        this.link = link;
    }

    protected Void call() {
        CompletableFuture.runAsync(this::createTasksAndUpdateUI, executor);
        return null;
    }

    private void createTasksAndUpdateUI() {
        if (getLoginSid() == null) {
            loginToDiskStation();
        }
        createTask();
        updateUIView(getDsTaskListDetail());
    }

    private void createTask() {
        ManageTaskFactoryBean factoryBean = new ManageTaskFactoryBean(
                getLoginSid(), dsApiDetail.getDownloadStationTask(), DSTaskMethod.CREATE_FROM_LINK, link
        );
        ManageTaskFactory.getManageTask(factoryBean).call();
        updateMessage("Torrents started");
        updateProgress(99, 100);
    }
}
