package pg.converter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pg.program.TaskDetail;
import pg.web.ds.DSTaskDownloadStatus;
import pg.web.ds.detail.DSAdditional;
import pg.web.ds.detail.DSFileDetail;
import pg.web.ds.detail.DSTask;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Created by Gawa 2018-03-03 */
@RunWith(MockitoJUnitRunner.class)
public class DSTaskToTaskDetailConverterTest {

    private Converter<DSTask, TaskDetail> converter;

    @Before
    public void setUp() {
        converter = new DSTaskToTaskDetailConverter();
    }

    @Test
    @Ignore
    public void givenDSTask_when_convert_then_returnTaskDetail() {
        List<TaskDetail> actual = converter.convert(Collections.singletonList(mockDsTask()));

        assertThat(actual, hasSize(1));
        TaskDetail actualTaskDetail = actual.get(0);
        assertThat(actualTaskDetail.getId(), is(equalTo("taskId")));
        assertThat(actualTaskDetail.getTitle(), is(equalTo("task title")));
        assertThat(actualTaskDetail.getProgress(), is(equalTo(33.0)));
    }

    private DSTask mockDsTask() {
        List<DSFileDetail> listFileDetail = createListFileDetail();
        DSAdditional additional = mockAdditional();
        when(additional.getFileDetails()).thenReturn(listFileDetail);
        DSTask task = mock(DSTask.class);
        when(task.getId()).thenReturn("taskId");
        when(task.getTitle()).thenReturn("task title");
        when(task.getStatus()).thenReturn(DSTaskDownloadStatus.downloading);
        when(task.getAdditional()).thenReturn(additional);
        return task;
    }

    private DSAdditional mockAdditional() {
        return mock(DSAdditional.class);
    }

    private List<DSFileDetail> createListFileDetail() {
        DSFileDetail file1 = mock(DSFileDetail.class);
        when(file1.getSize()).thenReturn("1000000");
        when(file1.getSizeDownloaded()).thenReturn("500000");

        DSFileDetail file2 = new DSFileDetail();
        when(file2.getSize()).thenReturn("2000000");
        when(file2.getSizeDownloaded()).thenReturn("500000");
        return Arrays.asList(file1, file2);
    }
}