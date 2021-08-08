package pg.converters;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pg.program.TaskDetail;
import pg.web.ds.DSTaskDownloadStatus;
import pg.web.ds.detail.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Created by Gawa 2018-03-03 */
@ExtendWith(MockitoExtension.class)
public class DSTaskToTaskDetailConverterTest {

    private Converter<DSTask, TaskDetail> converter;

    @BeforeEach
    public void setUp() {
        converter = new DSTaskToTaskDetailConverter();
    }

    @Test
    @Disabled
    public void givenDSTask_when_convert_then_returnTaskDetail() {
        List<TaskDetail> actual = Stream.of(converter.convert(mockDsTask())).collect(Collectors.toList());

        assertThat(actual).hasSize(1);
        TaskDetail actualTaskDetail = actual.get(0);
        assertThat(actualTaskDetail.getId()).isEqualTo("taskId");
        assertThat(actualTaskDetail.getTitle()).isEqualTo("task title");
        assertThat(actualTaskDetail.getProgress()).isEqualTo(33.0);
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