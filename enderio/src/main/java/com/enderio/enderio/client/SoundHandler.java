package com.enderio.enderio.client;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class SoundHandler {

    public static final Long2ObjectMap<SoundInstance> SOUND_MAP = new Long2ObjectArrayMap<>();

    public static void playSound(BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch, RandomSource random, double x, double y, double z) {
        SoundInstance sound = SOUND_MAP.getOrDefault(pos.asLong(), null);
        if (sound == null || !isActive(sound)) {
            sound = new SimpleSoundInstance(soundEvent, source, volume, pitch, random, x, y, z);
            SOUND_MAP.put(pos.asLong(), sound);
            Minecraft.getInstance().getSoundManager().play(sound);
        }
    }

    public static void stopSound(BlockPos pos) {
        SoundInstance sound = SOUND_MAP.getOrDefault(pos.asLong(), null);
        if (sound != null && isActive(sound)) {
            Minecraft.getInstance().getSoundManager().stop(sound);
            SOUND_MAP.put(pos.asLong(), null);
        }
    }

    public static boolean isActive(SoundInstance sound) {
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }
}
