package com.enderio.api.io;

import com.enderio.api.UseOnly;
import com.enderio.api.capability.IEnderCapabilityProvider;
import com.enderio.api.capability.ISideConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;

/**
 * IO Config defines how each side of a block interacts with other blocks.
 */
public interface IIOConfig extends INBTSerializable<CompoundTag>, IEnderCapabilityProvider<ISideConfig> {
    /**
     * Get the current IO mode for the given side.
     */
    IOMode getMode(Direction side);

    /**
     * Set the IO mode for this side.
     * Must be supported mode, otherwise {@link IOMode#NONE} will be set.
     */
    void setMode(Direction side, IOMode state);

    /**
     * Cycles through supported modes for this side.
     */
    default void cycleMode(Direction side) {
        IOMode currentMode = getMode(side);

        // Get the index of the current and next mode.
        int curOrd = currentMode.ordinal();
        int nextOrd = (curOrd + 1) % IOMode.values().length;

        // Cycle until we loop back on ourselves.
        while (nextOrd != curOrd) {
            IOMode next = IOMode.values()[nextOrd];

            if (supportsMode(side, next)) {
                setMode(side, next);
                break;
            }

            nextOrd = (nextOrd + 1) % IOMode.values().length;
        }
    }

    /**
     * Determine whether a certain side supports the provided mode.
     */
    boolean supportsMode(Direction side, IOMode state);

    /**
     * Whether the IO overlay should be rendered.
     */
    @UseOnly(LogicalSide.CLIENT)
    boolean renderOverlay();
}
