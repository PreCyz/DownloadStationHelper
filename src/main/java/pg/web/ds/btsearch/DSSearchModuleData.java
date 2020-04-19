package pg.web.ds.btsearch;

import lombok.Getter;
import pg.web.ds.DSGeneralResponse;

import java.util.List;

@Getter
public class DSSearchModuleData extends DSGeneralResponse {
    private List<DSSearchModule> modules;
}
