package pg.web.ds;

import lombok.Getter;

import java.util.List;

/** Created by Gawa 2017-11-12 */
@Getter
public class DSDataResponse extends DSGeneralResponse {

    private List<DSItem> data;

}
