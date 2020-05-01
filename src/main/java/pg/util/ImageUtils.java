package pg.util;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageUtils {

    public static Background getBackground(String imagePath) throws IOException {
        int width = 1;
        int height = 1;
        return getBackground(imagePath, width, height);
    }

    public static Background getBackground(String imagePath, double width, double height) throws IOException {
        try (InputStream is = ImageUtils.class.getClassLoader().getResourceAsStream(imagePath)) {
            return getBackground(new Image(is), width, height);
        }
    }

    public static Background getBackground(Image image, double width, double height) {
        BackgroundSize backgroundSize = new BackgroundSize(width, height, true, true, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        return new Background(backgroundImage);
    }
}
