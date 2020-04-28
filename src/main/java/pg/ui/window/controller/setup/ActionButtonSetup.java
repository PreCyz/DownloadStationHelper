package pg.ui.window.controller.setup;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.util.AppConstants;
import pg.util.ImageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Created by Gawa 2018-03-11 */
public class ActionButtonSetup implements ComponentSetup {

    private static final Logger logger = LoggerFactory.getLogger(ActionButtonSetup.class);

    private final double DEFAULT_WIDTH = 30;
    private final double DEFAULT_HEIGHT = 30;

    private final List<Button> buttons;
    private double width;
    private double height;
    private final boolean customSize;

    public ActionButtonSetup(List<Button> buttons) {
        this.buttons = buttons;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        customSize = false;
    }

    public ActionButtonSetup(List<Button> buttons, int width, int height) {
        this.buttons = buttons;
        this.width = width;
        this.height = height;
        customSize = true;
    }

    @Override
    public void setup() {
        for (Button button : buttons) {
            //setBackground(button);
            setupWithImage(button);
        }
    }

    private void setBackground(Button button) {
        try {
            button.setBackground(ImageUtils.getBackground(getButtonImage(button), width, height));
        } catch (IOException ex) {
            logger.error("Could not load button {} background image.", button.getId(), ex);
        }
    }

    private void setupWithImage(Button button) {
        try {
            ImageView imageView = new ImageView(getButtonImage(button));
            if (customSize) {
                imageView.setFitHeight(height);
                imageView.setFitWidth(width);
            } else {
                imageView.setFitHeight(button.getPrefHeight() - 10);
                imageView.setFitWidth(button.getPrefHeight() - 10);
            }
            button.setGraphic(imageView);
        } catch (IOException ex) {
            logger.error("Could not load button {} image.", button.getId(), ex);
        }
    }

    @NotNull
    private Image getButtonImage(Button button) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(getImagePath(button)) ) {
            return new Image(is);
        }
    }

    private String getImagePath(Button button) {
        return String.format("%s%s.png", AppConstants.IMG_RESOURCE_PATH, button.getId());
    }
}
