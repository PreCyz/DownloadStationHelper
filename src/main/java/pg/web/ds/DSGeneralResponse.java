package pg.web.ds;

import lombok.Getter;
import pg.web.ds.detail.DSErrorCode;

/**Created by Gawa on 26/08/17.*/
@Getter
public class DSGeneralResponse {

    private DSErrorCode error;
    private boolean success;

}
