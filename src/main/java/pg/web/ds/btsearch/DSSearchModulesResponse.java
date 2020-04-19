package pg.web.ds.btsearch;

import lombok.Getter;
import pg.web.ds.DSGeneralResponse;

@Getter
public class DSSearchModulesResponse extends DSGeneralResponse {
    private DSSearchModuleData data;
}
