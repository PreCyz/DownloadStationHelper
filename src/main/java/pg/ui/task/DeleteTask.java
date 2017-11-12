package pg.ui.task;

import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import pg.ui.task.atomic.AppTask;
import pg.ui.task.atomic.call.ds.DeleteDSTaskCall;
import pg.ui.task.atomic.call.ds.ListOfDSTaskCall;
import pg.web.model.ApiDetails;
import pg.web.model.torrent.ReducedDetail;
import pg.web.response.detail.DSTask;
import pg.web.response.detail.TaskListDetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Created by Gawa 2017-10-29
 */
public class DeleteTask extends Task<Void> {

    private final ListView<ReducedDetail> listView;
    private final ExecutorService executor;
    private final String sid;
    private final ApiDetails downloadStationTask;
    private Collection<ReducedDetail> torrentsToDelete;

    public DeleteTask(ListView<ReducedDetail> listView, String sid, ApiDetails downloadStationTask, Collection<ReducedDetail> torrentsToDelete,
                      ExecutorService executor) {
        this.listView = listView;
        this.torrentsToDelete = torrentsToDelete;
        this.executor = executor;
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
    }

    @Override
    protected Void call() throws Exception {
        AppTask<TaskListDetail> taskList = new AppTask<>(new ListOfDSTaskCall(sid, downloadStationTask), executor);
        List<DSTask> tasksToDelete = new ArrayList<>();
        List<DSTask> allTasks = taskList.get().getTasks();
        for (ReducedDetail detail : torrentsToDelete) {
            tasksToDelete.addAll(allTasks.stream()
                    .filter(t -> t.getTitle().equals(detail.getTitle()))
                    .collect(Collectors.toList())
            );
        }

        new AppTask<>(new DeleteDSTaskCall(sid, tasksToDelete, downloadStationTask), executor);

        return null;
    }
}
