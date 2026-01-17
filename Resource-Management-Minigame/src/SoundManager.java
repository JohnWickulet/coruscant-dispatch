import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    public static void playSound(String fileName) {
        try {
            File soundFile = new File("src/sounds/" + fileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
}