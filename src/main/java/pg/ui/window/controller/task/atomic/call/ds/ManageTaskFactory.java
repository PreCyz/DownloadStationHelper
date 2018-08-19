package pg.ui.window.controller.task.atomic.call.ds;

import pg.exception.ProgramException;
import pg.exception.UIError;

public final class ManageTaskFactory {
    private ManageTaskFactory() { }

    public static ManageTaskCall getManageTask(ManageTaskFactoryBean bean) {
        switch (bean.getTaskMethod()) {
            case RESUME:
                return new ResumeTaskCall(bean.getSid(), bean.getTasksToManage(), bean.getDownloadStationTask());
            case DELETE:
                return new DeleteTaskCall(bean.getSid(), bean.getTasksToManage(), bean.getDownloadStationTask());
            case PAUSE:
                return new PauseTaskCall(bean.getSid(), bean.getTasksToManage(), bean.getDownloadStationTask());
            case DELETE_FORCE:
                return new DeleteForceCompleteTaskCall(bean.getSid(), bean.getTasksToManage(), bean.getDownloadStationTask());
            case CREATE:
                return new CreateTaskCall(bean.getSid(), bean.getMatchTorrents(), bean.getDownloadStationTask());
            case CREATE_FROM_LINK:
                return new CreateTaskFromLinkCall(bean.getSid(), bean.getTorrentUri(), bean.getDownloadStationTask());
        }
        throw new ProgramException(UIError.LAUNCH_PROGRAM);
    }
}
