package pg.ui.window.controller.completable;

import javafx.scene.control.ListView;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.task.atomic.call.ds.CreateTaskFromLinkCall;
import pg.web.ds.detail.DSTask;
import pg.web.ds.detail.DsApiDetail;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/** Created by Gawa 2018-02-10 */
public class UseLinkTaskCompletable extends ListTaskCompletable {

    private String link;

    public UseLinkTaskCompletable(ListView<DSTask> listView, DsApiDetail dsApiDetail, WindowHandler windowHandler,
                                    String link, ExecutorService executor) {
        super(listView, dsApiDetail, windowHandler, executor);
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
        new CreateTaskFromLinkCall(getLoginSid(), link, dsApiDetail.getDownloadStationTask()).run();
        updateMessage("Torrents started");
        updateProgress(99, 100);
    }
}
