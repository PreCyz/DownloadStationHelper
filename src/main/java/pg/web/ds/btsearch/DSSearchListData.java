package pg.web.ds.btsearch;

import lombok.Getter;

import java.util.List;

@Getter
public class DSSearchListData {
    private boolean finished;
    private int offset;
    private int total;
    private List<DSSearchListItem> items;
}
