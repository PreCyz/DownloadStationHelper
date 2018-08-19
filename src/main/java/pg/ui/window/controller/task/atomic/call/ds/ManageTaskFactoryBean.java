package pg.ui.window.controller.task.atomic.call.ds;

import lombok.Getter;
import pg.program.TaskDetail;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;
import pg.web.torrent.ReducedDetail;

import java.util.List;

@Getter
public final class ManageTaskFactoryBean {

    private String sid;
    private List<TaskDetail> tasksToManage;
    private DSApiDetails downloadStationTask;
    private DSTaskMethod taskMethod;
    private List<ReducedDetail> matchTorrents;
    private String torrentUri;

    private ManageTaskFactoryBean(String sid, DSApiDetails downloadStationTask, DSTaskMethod taskMethod) {
        this.sid = sid;
        this.downloadStationTask = downloadStationTask;
        this.taskMethod = taskMethod;
    }

    public ManageTaskFactoryBean(String sid, List<TaskDetail> tasksToManage, DSApiDetails downloadStationTask,
                                 DSTaskMethod taskMethod) {
        this(sid, downloadStationTask, taskMethod);
        this.tasksToManage = tasksToManage;
    }

    public ManageTaskFactoryBean(String sid, DSApiDetails downloadStationTask, DSTaskMethod taskMethod,
                                 List<ReducedDetail> matchTorrents) {
        this(sid, downloadStationTask, taskMethod);
        this.matchTorrents = matchTorrents;
    }

    public ManageTaskFactoryBean(String sid, DSApiDetails downloadStationTask, DSTaskMethod taskMethod, String torrentUri) {
        this(sid, downloadStationTask, taskMethod);
        this.torrentUri = torrentUri;
    }
}
