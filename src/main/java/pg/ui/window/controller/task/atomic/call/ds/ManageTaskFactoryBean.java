package pg.ui.window.controller.task.atomic.call.ds;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.program.TaskDetail;
import pg.web.ds.DSTaskMethod;
import pg.web.ds.detail.DSApiDetails;

import java.util.List;

@AllArgsConstructor
@Getter
public final class ManageTaskFactoryBean {

    private String sid;
    private List<TaskDetail> tasksToManage;
    private DSApiDetails downloadStationTask;
    private DSTaskMethod taskMethod;
}
