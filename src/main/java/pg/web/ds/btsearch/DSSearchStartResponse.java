package pg.web.ds.btsearch;

import lombok.Getter;
import pg.web.ds.DSGeneralResponse;

@Getter
public class DSSearchStartResponse extends DSGeneralResponse {
    private DSSearchStartData data;
}
