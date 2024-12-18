package main;

import javax.sound.sampled.*;
import java.io.*;

public class SoundHandler {
    Clip clip1;
    Clip clip2;
    AudioInputStream sound1;
    AudioInputStream sound2;
    InputStream file1;
    InputStream file2;
    boolean twoFiles = false;
    int numberOfPlays = 0;

    public SoundHandler(String soundFileName) {
        setFile(soundFileName);
    }

    public SoundHandler(String soundFileName1, String soundFileName2) {
        setFile(soundFileName1, soundFileName2);
        twoFiles = true;
    }
    public void setFile(String soundFileName1) {
        try {
            file1 = this.getClass().getClassLoader().getResourceAsStream(soundFileName1);
            assert file1 != null;
            InputStream bufferedIn = new BufferedInputStream(file1);
            sound1 = AudioSystem.getAudioInputStream(bufferedIn);
            clip1 = AudioSystem.getClip();
            clip1.open(sound1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFile(String soundFileName1, String soundFileName2) {
        try {
            file1 = this.getClass().getClassLoader().getResourceAsStream(soundFileName1);
            file2 = this.getClass().getClassLoader().getResourceAsStream(soundFileName2);
            InputStream bufferedIn1 = new BufferedInputStream(file1);
            InputStream bufferedIn2 = new BufferedInputStream(file2);

            sound1 = AudioSystem.getAudioInputStream(bufferedIn1);
            sound2 = AudioSystem.getAudioInputStream(bufferedIn2);

            clip1 = AudioSystem.getClip();
            clip1.open(sound1);
            clip2 = AudioSystem.getClip();
            clip2.open(sound2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        stop();
        run();
    }

    private void run() {
        if (!twoFiles || numberOfPlays % 2 == 0) {
            clip1.start();
        } else {
            clip2.start();
        }
        numberOfPlays++;

    }
    private void stop() {
        try {
            if (!twoFiles || numberOfPlays % 2 == 1) {
                clip1.stop();
                clip1.flush();
                clip1.setFramePosition(0);
//                clip1.stop();
//                clip1.close();
//                sound1.close();
//                sound1 = AudioSystem.getAudioInputStream(file1);
//                clip1 = AudioSystem.getClip();
//                clip1.open(sound1);
            } else {
                clip2.stop();
                clip2.flush();
                clip2.setFramePosition(0);
//                clip2.stop();
//                clip2.close();
//                sound2.close();
//                sound2 = AudioSystem.getAudioInputStream(file2);
//                clip2 = AudioSystem.getClip();
//                clip2.open(sound2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}