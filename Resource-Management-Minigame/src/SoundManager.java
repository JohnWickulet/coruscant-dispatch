import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class SoundManager {
    static Random rng =new Random();
    public static void playSound(String fileName) {
        try {
            URL url = SoundManager.class.getResource("/sounds/" + fileName);
            AudioInputStream baseStream = AudioSystem.getAudioInputStream(url);
            AudioFormat baseFormat = baseStream.getFormat();

            // Check if we need to convert from 6 channels to 2
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,    // sample size in bits
                    2,     // channels (forcing Stereo)
                    4,     // frame size
                    baseFormat.getSampleRate(),
                    false  // bigEndian
            );

            if (!AudioSystem.isLineSupported(new DataLine.Info(Clip.class, baseFormat))) {
                // Convert the stream to the target stereo format
                AudioInputStream outStream = AudioSystem.getAudioInputStream(targetFormat, baseStream);
                Clip clip = AudioSystem.getClip();
                clip.open(outStream);
                clip.start();
            } else {
                Clip clip = AudioSystem.getClip();
                clip.open(baseStream);
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
    public static void selectTroop(){
        String[] sounds={"troop_select1.wav","troop_select2.wav","troop_select3.wav"};
        SoundManager.playSound(sounds[rng.nextInt(sounds.length)]);
    }
    public static void event(){
        String[] sounds={"event_popup1.wav","event_popup2.wav"};
        SoundManager.playSound(sounds[rng.nextInt(sounds.length)]);
    }
    public static void upgradeBig(){
        SoundManager.playSound("upgrade.wav");
    }
    public static void upgradeSmall(){
        SoundManager.playSound("upgrade1.wav");
    }
    public static void select(){
        String[] sounds={"select1.wav","select2.wav","select3.wav","select4.wav","select5.wav"};
        SoundManager.playSound(sounds[rng.nextInt(sounds.length)]);
    }
    public static void heal(){
        String[] sounds={"heal1.wav","heal2.wav"};
        SoundManager.playSound(sounds[rng.nextInt(sounds.length)]);
    }
    public static void error(){
        SoundManager.playSound("error.wav");
    }
    public static void selectItem(){
        String[] sounds={"item_select1.wav","item_select2.wav"};
        SoundManager.playSound(sounds[rng.nextInt(sounds.length)]);
    }
}