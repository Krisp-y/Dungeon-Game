package unsw.dungeon;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.Scene;
// import com.sun.media.jfxmediaimpl.NativeMediaManager;

public class SoundController {
    private MediaPlayer bgMusicPlayer;
    private static SoundController sc;
    
    private SoundController() {
        
    }
    
    public static SoundController getSC() {
        if (sc == null) {
            sc = new SoundController();
        }
        return sc;
    }
    
    public void setBgMusic(String musicFile) {
        String f = new File(musicFile).toURI().toString();
        Media m = new Media(f);
        bgMusicPlayer = new MediaPlayer(m);
        bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        playBgMusic();
    }
    
    public void playBgMusic() {
        if (bgMusicPlayer != null) {
            bgMusicPlayer.play();
        }
    }
    
    public void stopBgMusic() {
        if (bgMusicPlayer != null) {
            bgMusicPlayer.pause();
        }
    }
    
    public double getVolume() {
        return bgMusicPlayer.getVolume();
    }
    
    public void setVolume(double volume) {
       bgMusicPlayer.setVolume(volume);
    }
    
    private static MediaPlayer getfxplayer(String soundFile) {
        Media m = new Media(new File(soundFile).toURI().toString());
        return new MediaPlayer(m);
    }

    public static void playPlayerHitSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/player_hit.wav");
        fxplayer.play();
    }
    
    public static void playEnemyHitSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/enemy_hit.mp3");
        fxplayer.play();
    
    }
    
    public static void playCoinsCollectedSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/coins_collected.mp3");
        fxplayer.play();
    
    }
    
    public static void playBoulderPushedSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/boulder_pushed.mp3");
        fxplayer.play();
    }
    
    public static void playSwitchTriggeredSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/switch_triggered.wav");
        fxplayer.play();
    }
    
    public static void playExitIncompleteSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/exit_incomplete.wav");
        fxplayer.play();
    }
    
    public static void playPotionCollectedSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/object_collected.mp3");
        fxplayer.play();
    }
    
    public static void playSwordCollectedSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/swod_collected.mp3");
        fxplayer.play();
    }
    
    public static void playFireballSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/fireball.mp3");
        fxplayer.play();
    }
    
    public static void playGameOverSound() {
        MediaPlayer fxplayer = getfxplayer("sounds/game_over.mp3");
        fxplayer.play();
    }
    
    public static void playVictorySound() {
        MediaPlayer fxplayer = getfxplayer("sounds/victory.mp3");
        fxplayer.play();
    }
    
}