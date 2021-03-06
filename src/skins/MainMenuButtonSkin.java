package skins;

import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;

public class MainMenuButtonSkin extends ButtonSkin {
    public MainMenuButtonSkin(Button control) {
        super(control);

        final FadeTransition fadeIn = new FadeTransition(Duration.millis(300));
        fadeIn.setNode(control);
        fadeIn.setToValue(1);
        control.setOnMouseEntered(e -> fadeIn.playFromStart());

        final FadeTransition fadeOut = new FadeTransition(Duration.millis(300));
        fadeOut.setNode(control);
        fadeOut.setToValue(0.2);
        control.setOnMouseExited(e -> fadeOut.playFromStart());

        control.setOpacity(0.2);
        
    }
    
    
}