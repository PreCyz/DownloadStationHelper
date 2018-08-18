package pg.ui.window.controller.task.atomic.call.ds;

import pg.exception.ProgramException;
import pg.exception.UIError;

public final class ManageTaskFactory {
    private ManageTaskFactory() { }

    public static ManageTaskCall getManageTask(ManageTaskFactoryBean bean) {
        switch (bean.getTaskMethod()) {
            case RESUME:
                return new ResumeCall(bean.getSid(), bean.getTasksToManage(), bean.getDownloadStationTask());
            case DELETE:
                return new DeleteCall(bean.getSid(), bean.getTasksToManage(), bean.getDownloadStationTask());
            case PAUSE:
                return new PauseCall(bean.getSid(), bean.getTasksToManage(), bean.getDownloadStationTask());
            case DELETE_FORCE:
                return new DeleteForceCompleteCall(bean.getSid(), bean.getTasksToManage(), bean.getDownloadStationTask());
        }
        throw new ProgramException(UIError.LAUNCH_PROGRAM);
    }
}
