package pg.web.ds.btsearch;

import lombok.Getter;
import pg.web.ds.DSGeneralResponse;

@Getter
public class DSSearchListResponse extends DSGeneralResponse {
    private DSSearchListData data;
}
