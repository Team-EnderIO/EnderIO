package com.enderio.enderio.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;

public class SoundHandler {

    public static void playSound(SoundInstance sound) {
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    public static void stopSound(SoundInstance sound) {
        Minecraft.getInstance().getSoundManager().stop(sound);
    }

    public static boolean isActive(SoundInstance sound) {
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }
}
