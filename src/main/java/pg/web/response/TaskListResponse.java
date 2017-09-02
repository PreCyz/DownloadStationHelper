package pg.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import pg.web.response.detail.TaskListDetail;

/**Created by Gawa on 27/08/17.*/
public class TaskListResponse extends GeneralResponse {
    @JsonProperty("data")
    private TaskListDetail taskListDetail;

    public TaskListDetail getTaskListDetail() {
        return taskListDetail;
    }
}
