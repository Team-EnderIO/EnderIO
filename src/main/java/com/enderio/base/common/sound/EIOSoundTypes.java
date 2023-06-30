package com.enderio.base.common.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;

public class EIOSoundTypes {
    public static final SoundType EMPTY = new ForgeSoundType(1.0F, 1.0F, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY);
}
