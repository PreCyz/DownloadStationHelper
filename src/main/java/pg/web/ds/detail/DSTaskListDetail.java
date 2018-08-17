package pg.web.ds.detail;

import lombok.Getter;

import java.util.List;

/**Created by Gawa on 27/08/17.*/
@Getter
public class DSTaskListDetail {

    private int offset;
    private List<DSTask> tasks;
    private int total;

}
