package com.enderio.base.common.block;

import com.enderio.base.common.sound.EIOSoundTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class EIOBlockSetType {
    // TODO: NEO-PORT: Check Pressure sensitivity
    public static final BlockSetType SILENT = BlockSetType.register(new BlockSetType("enderio:block.silent", true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, EIOSoundTypes.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY));
}
