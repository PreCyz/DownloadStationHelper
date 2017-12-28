package pg.web.response;

import pg.web.response.detail.ErrorCode;

/**Created by Gawa on 26/08/17.*/
public class DSGeneralResponse {

    private ErrorCode error;
    private boolean success;

    public ErrorCode getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }
}
