package com.enderio.base.common.blockentity;

import com.enderio.api.capability.ISideConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumMap;
import java.util.Map;

// TODO: Way for IOConfig to have disabled sides.

/**
 * Represents the state of each side of the block
 */
public class IOConfig implements INBTSerializable<CompoundTag> {

    public enum State {
        /**
         * No specific configuration, allows external input and output but doesn't pull or push itself.
         */
        NONE(true, true, false),

        /**
         * Only pushes outputs allows both external pulling and the machine pushes itself.
         * <p>
         * For example conduits can pull themselves, however putting a chest next to the machine will cause it to push items to the chest.
         *
         * @apiNote Each machine determines what this means this for energy. Some may ignore it.
         */
        PUSH(false, true, true),

        /**
         * Only pulls inputs, allowing both external pushing and the machine pulling itself.
         * <p>
         * For example conduits can push into the machine themselves, but a chest next to the machine will also be pulled from.
         *
         * @apiNote Each machine determines what this means this for energy. Some may ignore it.
         */
        PULL(true, false, true),

        /**
         * Allow both pulling and pushing by both the machine and external blocks.
         *
         * @apiNote Each machine determines what this means this for energy. Some may ignore it.
         */
        BOTH(true, true, true),

        /**
         * Disallow any side access for all resources (including energy).
         *
         * @apiNote All machines will disallow power access for this side.
         */
        DISABLED(false, false, false);

        private final boolean input, output, force;

        State(boolean input, boolean output, boolean force) {
            this.input = input;
            this.output = output;
            this.force = force;
        }

        /**
         * Can resources be input via this side.
         */
        public boolean canInput() {
            return input;
        }

        /**
         * Can resources be output via this side.
         */
        public boolean canOutput() {
            return output;
        }

        /**
         * Can resources be pushed out this side.
         *
         * @implNote This can be used by machines to determine if it should push resources out.
         */
        public boolean canPush() {
            return canOutput() && canForce();
        }

        /**
         * Can resources be pulled in this side.
         *
         * @implNote This can be used by machines to determine if it should pull resources in.
         */
        public boolean canPull() {
            return canInput() && canForce();
        }

        /**
         * Whether the machine can force resources in/out this side.
         */
        public boolean canForce() {
            return force;
        }
    }

    private final EnumMap<Direction, State> config = new EnumMap<>(Direction.class);

    private final EnumMap<Direction, LazyOptional<SideAccess>> sideAccessCache = new EnumMap<>(Direction.class);

    public IOConfig() {
        for (Direction value : Direction.values()) {
            config.put(value, State.NONE);
        }
    }

    /**
     * Get the IO state of the given side.
     */
    public State getSide(Direction side) {
        return config.get(translateSide(side));
    }

    /**
     * Set the IO state of the given side.
     */
    public void setSide(Direction side, State state) {
        config.put(translateSide(side), state);
        onChanged();
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
        for (Map.Entry<Direction, State> entry : config.entrySet()) {
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
            config.put(Direction.values()[entryNbt.getInt("direction")], State.values()[entryNbt.getInt("state")]);
        }
    }

    // For providing sided access via a capability.
    private record SideAccess(IOConfig config, Direction side) implements ISideConfig {
        @Override
        public State getState() {
            return config.getSide(side);
        }

        @Override
        public void setState(State state) {
            config.setSide(side, state);
        }
    }
}
