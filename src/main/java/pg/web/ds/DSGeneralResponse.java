package pg.web.ds;

import pg.web.ds.detail.DSErrorCode;

/**Created by Gawa on 26/08/17.*/
public class DSGeneralResponse {

    private DSErrorCode error;
    private boolean success;

    public DSErrorCode getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }
}
