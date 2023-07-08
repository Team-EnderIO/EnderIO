package com.enderio.machines.common.io;

import com.enderio.api.capability.ISideConfig;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents the state of each side of the block
 */
public class IOConfig implements IIOConfig {

    private final EnumMap<Direction, IOMode> config = new EnumMap<>(Direction.class);

    private final EnumMap<Direction, LazyOptional<SideAccess>> sideAccessCache = new EnumMap<>(Direction.class);

    public IOConfig() {
        for (Direction value : Direction.values()) {
            config.put(value, IOMode.NONE);
        }
    }

    @Override
    public IOMode getMode(Direction side) {
        return config.get(translateSide(side));
    }

    @Override
    public void setMode(Direction side, IOMode mode) {
        Direction relSide = translateSide(side);
        IOMode oldMode = config.get(relSide);
        config.put(relSide, mode);
        onChanged(side, oldMode, mode);
    }

    @Override
    public boolean supportsMode(Direction side, IOMode state) {
        return true;
    }

    @Override
    public boolean renderOverlay() {
        return true;
    }

    // Translates world side -> rotated side.
    private Direction translateSide(Direction side) {
        // The block faces with its southern face. So the back of the machine.
        Direction south = getBlockFacing();
        return switch (side) {
        case NORTH -> south.getOpposite();
        case SOUTH -> south;
        case WEST -> south.getCounterClockWise();
        case EAST -> south.getClockWise();
        default -> side;
        };
    }

    // region Capability Provider

    @Override
    public Capability<ISideConfig> getCapabilityType() {
        return EIOCapabilities.SIDE_CONFIG;
    }

    /**
     * Get side config as a capability.
     */
    public LazyOptional<ISideConfig> getCapability(@Nullable Direction side) {
        if (side == null)
            return LazyOptional.empty();
        return sideAccessCache.computeIfAbsent(side, dir -> LazyOptional.of(() -> new SideAccess(this, dir))).cast();
    }

    @Override
    public void invalidateSide(@Nullable Direction side) {
        if (side == null)
            return;

        if (sideAccessCache.containsKey(side)) {
            sideAccessCache.get(side).invalidate();
            sideAccessCache.remove(side);
        }
    }

    /**
     * Invalidate any side capabilities.
     */
    public void invalidateCaps() {
        for (LazyOptional<SideAccess> access : sideAccessCache.values()) {
            access.invalidate();
        }
    }

    // endregion

    // Override in a BE
    protected void onChanged(Direction side, IOMode oldMode, IOMode newMode) {

    }

    // Override if the machine can be rotated.
    protected Direction getBlockFacing() {
        return Direction.SOUTH;
    }

    // region Serialization

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<Direction, IOMode> entry : config.entrySet()) {
            nbt.putByte(entry.getKey().get3DDataValue() + "", (byte)entry.getValue().ordinal());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (String key: nbt.getAllKeys()) {
            if (nbt.contains(key, Tag.TAG_BYTE)) {
                try {
                    int i = Integer.parseInt(key);
                    if (i >= 0 && i < Direction.values().length) {
                        Direction direction = Direction.from3DDataValue(i);
                        byte ioModeIndex = nbt.getByte(key);
                        if (ioModeIndex >= 0 && ioModeIndex < IOMode.values().length) {
                            config.put(direction, IOMode.values()[ioModeIndex]);
                        }
                    }
                } catch (NumberFormatException ignored) {

                }
            }
        }
    }

    // endregion

    // region Hashing

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IOConfig ioConfig = (IOConfig) o;

        return config.equals(ioConfig.config);
    }

    @Override
    public int hashCode() {
        return config.hashCode();
    }

    // endregion

    // For providing sided access via a capability.
    private record SideAccess(IOConfig config, Direction side) implements ISideConfig {
        @Override
        public IOMode getMode() {
            return config.getMode(side);
        }

        @Override
        public void setMode(IOMode mode) {
            config.setMode(side, mode);
        }

        @Override
        public void cycleMode() {
            config.cycleMode(side);
        }
    }
}
