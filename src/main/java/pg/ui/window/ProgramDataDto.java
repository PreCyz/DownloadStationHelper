package pg.ui.window;

import javafx.stage.Window;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pg.web.ds.detail.DsApiDetail;

@Getter
@Setter
@NoArgsConstructor
public class ProgramDataDto {
    private Window window;
    private DsApiDetail dsApiDetail;
    private boolean isLoggedIn;
    private String searchTaskId;
}