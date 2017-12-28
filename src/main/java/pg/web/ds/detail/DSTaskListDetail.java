package pg.web.ds.detail;

import java.util.List;

/**Created by Gawa on 27/08/17.*/
public class DSTaskListDetail {
    private int offset;
    private List<DSTask> tasks;
    private int total;

    public int getOffset() {
        return offset;
    }

    public List<DSTask> getTasks() {
        return tasks;
    }

    public int getTotal() {
        return total;
    }
}
