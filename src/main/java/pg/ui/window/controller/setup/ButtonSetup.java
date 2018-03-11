package pg.ui.window.controller.setup;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import pg.util.AppConstants;

import java.io.InputStream;
import java.util.List;

/* Created by Gawa 2018-03-11 */
public class ButtonSetup implements ComponentSetup {

    private List<Button> buttons;

    public ButtonSetup(List<Button> buttons) {
        this.buttons = buttons;
    }

    @Override
    public void setup() {
        for (Button button : buttons) {
            setBackground(button);
        }
    }

    private void setBackground(Button button) {
        int width = 30;
        int height = 30;
        BackgroundSize backgroundSize = new BackgroundSize(width, height, false, false, false, false);
        String imagePath = String.format("%s%s.png", AppConstants.IMG_RESOURCE_PATH, button.getId());
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(imagePath);
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(inputStream),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    private void setupWithImage(Button button) {
        String imagePath = String.format("%s%s", AppConstants.IMG_RESOURCE_PATH, button.getId());
        ImageView addImg = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(imagePath)));
        addImg.setFitHeight(30);
        addImg.setFitWidth(30);
        button.setGraphic(addImg);
    }
}
