package pg.converter;

import pg.program.TaskDetail;
import pg.web.ds.detail.DSAdditional;
import pg.web.ds.detail.DSTask;

/** Created by Gawa 2018-03-03 */
public class DSTaskToTaskDetailConverter extends AbstractConverter<DSTask, TaskDetail> {

    @Override
    public TaskDetail convert(DSTask source) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setId(source.getId());
        taskDetail.setTitle(source.getTitle());
        taskDetail.setStatus(source.getStatus().name());
        taskDetail.setProgress(calculateProgress(source));
        return taskDetail;
    }

    private double calculateProgress(DSTask dsTask) {
        DSAdditional additional = dsTask.getAdditional();
        long totalSize = additional.getFileDetails().stream().mapToLong(item -> Long.parseLong(item.getSize())).sum();
        long downloadedSize = additional.getFileDetails().stream().mapToLong(item -> Long.parseLong(item.getSizeDownloaded())).sum();
        if (totalSize == 0) {
            return Double.NaN;
        }
        return 100 * downloadedSize / totalSize;
    }
}
