package pg.ui.window.controller.setup;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;
import pg.util.AppConstants;

import java.util.List;

/* Created by Gawa 2018-03-11 */
public class ActionButtonSetup implements ComponentSetup {

    private List<Button> buttons;

    public ActionButtonSetup(List<Button> buttons) {
        this.buttons = buttons;
    }

    @Override
    public void setup() {
        for (Button button : buttons) {
            //setBackground(button);
            setupWithImage(button);
        }
    }

    private void setBackground(Button button) {
        int width = 30;
        int height = 30;
        BackgroundSize backgroundSize = new BackgroundSize(width, height, false, false, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                getButtonImage(button),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    private void setupWithImage(Button button) {
        ImageView imageView = new ImageView(getButtonImage(button));
        imageView.setFitHeight(button.getPrefHeight() - 10);
        imageView.setFitWidth(button.getPrefHeight() - 10);
        button.setGraphic(imageView);
        /*System.out.printf("Button: %s [width,height] -> [%f,%f]%n",
                button.getId(), button.getPrefWidth(), button.getPrefHeight());*/
    }

    @NotNull
    private Image getButtonImage(Button button) {
        return new Image(getClass().getClassLoader().getResourceAsStream(getImagePath(button)));
    }

    private String getImagePath(Button button) {
        return String.format("%s%s.png", AppConstants.IMG_RESOURCE_PATH, button.getId());
    }
}
