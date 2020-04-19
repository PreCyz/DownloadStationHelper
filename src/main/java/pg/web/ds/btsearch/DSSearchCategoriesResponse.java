package pg.web.ds.btsearch;

import lombok.Getter;
import pg.web.ds.DSGeneralResponse;

@Getter
public class DSSearchCategoriesResponse extends DSGeneralResponse {
    private DSSearchCategoryData data;
}
