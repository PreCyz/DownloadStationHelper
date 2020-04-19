package pg.converters;

import pg.program.TaskDetail;
import pg.web.ds.DSTaskDownloadStatus;
import pg.web.ds.detail.DSAdditional;
import pg.web.ds.detail.DSTask;

import java.util.EnumSet;

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
        final double COMPLETED = 100d;
        if (EnumSet.of(DSTaskDownloadStatus.finished, DSTaskDownloadStatus.finishing).contains(dsTask.getStatus())) {
            return COMPLETED;
        } else if (dsTask.getStatus() == DSTaskDownloadStatus.downloading) {
            DSAdditional additional = dsTask.getAdditional();
            if (dsTask.getSize() == 0 || additional.getFileDetails() == null || additional.getFileDetails().isEmpty()) {
                return Double.NaN;
            }
            long downloadedSize = additional.getFileDetails()
                    .stream()
                    .mapToLong(item -> Long.parseLong(item.getSizeDownloaded()))
                    .sum();
            double progress = COMPLETED * downloadedSize / dsTask.getSize();
            return (int) (COMPLETED * progress) / COMPLETED;
        }
        return Double.NaN;
    }
}
