package com.enderio.machines.common.io;

import com.enderio.api.capability.ISideConfig;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.LazyOptional;

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
    public void setMode(Direction side, IOMode state) {
        config.put(translateSide(side), state);
        onChanged();
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

    /**
     * Get side config as a capability.
     */
    public LazyOptional<ISideConfig> getCapabilityFor(Direction side) {
        return sideAccessCache.computeIfAbsent(side, dir -> LazyOptional.of(() -> new SideAccess(this, dir))).cast();
    }

    /**
     * Invalidate any side capabilities.
     */
    public void invalidateCaps() {
        for (LazyOptional<SideAccess> access : sideAccessCache.values()) {
            access.invalidate();
        }
    }

    // Override in a BE
    protected void onChanged() {

    }

    // Override if the machine can be rotated.
    protected Direction getBlockFacing() {
        return Direction.SOUTH;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag listNbt = new ListTag();
        for (Map.Entry<Direction, IOMode> entry : config.entrySet()) {
            CompoundTag entryNbt = new CompoundTag();
            entryNbt.putInt("direction", entry.getKey().ordinal());
            entryNbt.putInt("state", entry.getValue().ordinal());
            listNbt.add(entryNbt);
        }
        nbt.put("data", listNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag listNbt = nbt.getList("data", Tag.TAG_COMPOUND);
        for (Tag tag : listNbt) {
            CompoundTag entryNbt = (CompoundTag) tag;
            config.put(Direction.values()[entryNbt.getInt("direction")], IOMode.values()[entryNbt.getInt("state")]);
        }
    }

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
