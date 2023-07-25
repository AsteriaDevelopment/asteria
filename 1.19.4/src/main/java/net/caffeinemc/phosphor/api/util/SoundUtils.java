package net.caffeinemc.phosphor.api.util;

import net.caffeinemc.phosphor.common.Phosphor;
import net.caffeinemc.phosphor.module.modules.client.AsteriaSettingsModule;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundUtils {
    public static void playSound(InputStream inputStream) {
        if (!Phosphor.moduleManager().getModule(AsteriaSettingsModule.class).clickSounds.isEnabled()) return;

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(inputStream);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {}
    }

    public static void playSound(String pathToAudio) {
        playSound(SoundUtils.class.getClassLoader().getResourceAsStream(pathToAudio));
    }
}
