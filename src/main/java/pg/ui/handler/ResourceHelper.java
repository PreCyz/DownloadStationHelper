package pg.ui.handler;

import javafx.scene.image.Image;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;

import java.io.InputStream;

/**Created by Gawa */
public final class ResourceHelper {

    private ResourceHelper() {}
    
    public static Image readImage(String imgPath) throws ProgramException {
        try (InputStream is = ResourceHelper.class.getClassLoader().getResourceAsStream(imgPath)) {
            return new Image(is);
        } catch (Exception ex) {
            throw new ProgramException(UIError.BUTTON_IMG, imgPath, ex);
        }
    }

}
