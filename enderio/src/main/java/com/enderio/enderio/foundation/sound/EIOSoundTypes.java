package com.enderio.enderio.foundation.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.DeferredSoundType;

public class EIOSoundTypes {
    public static final SoundType EMPTY = new DeferredSoundType(1.0F, 1.0F, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY);
}
