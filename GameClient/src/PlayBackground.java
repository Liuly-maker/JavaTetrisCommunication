import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;


public class PlayBackground extends Application {
    final JFXPanel fxPanel = new JFXPanel();
    @Override
    public void start(Stage stage) throws Exception {
        Media sound = new Media(new File("./music.wav").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
}

