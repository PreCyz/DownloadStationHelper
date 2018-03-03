package pg.converter;

import org.junit.Before;
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
    public void givenDSTask_when_convert_then_returnTaskDetail() {
        List<TaskDetail> actual = converter.convert(Collections.singletonList(mockDsTask()));

        assertThat(actual, hasSize(1));
        TaskDetail actualTaskDetail = actual.get(0);
        assertThat(actualTaskDetail.getId(), is(equalTo("taskId")));
        assertThat(actualTaskDetail.getTitle(), is(equalTo("task title")));
        assertThat(actualTaskDetail.getProgress(), is(equalTo(33.0)));
    }

    private DSTask mockDsTask() {
        DSAdditional additional = mockAdditional();
        DSTask task = mock(DSTask.class);
        when(task.getId()).thenReturn("taskId");
        when(task.getTitle()).thenReturn("task title");
        when(task.getStatus()).thenReturn(DSTaskDownloadStatus.downloading);
        when(task.getAdditional()).thenReturn(additional);
        return task;
    }

    private DSAdditional mockAdditional() {
        DSAdditional mock = mock(DSAdditional.class);
        when(mock.getFileDetails()).thenReturn(createListFileDetail());
        return mock;
    }

    private List<DSFileDetail> createListFileDetail() {
        DSFileDetail file1 = new DSFileDetail();
        file1.setSize("1000000");
        file1.setSizeDownloaded("500000");

        DSFileDetail file2 = new DSFileDetail();
        file2.setSize("2000000");
        file2.setSizeDownloaded("500000");
        return Arrays.asList(file1, file2);
    }
}