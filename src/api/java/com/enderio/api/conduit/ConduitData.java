package com.enderio.api.conduit;

import com.enderio.api.UseOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * used for special single use things like RoundRobin for ItemConduits or proxying Caps
 */
public interface ConduitData<T extends ConduitData<T>> extends INBTSerializable<CompoundTag> {

    EmptyConduitData EMPTY = new EmptyConduitData();

    // region Events

    default void onCreated(ConduitType<?> type, Level level, BlockPos pos, @Nullable Player player) {}

    default void onRemoved(ConduitType<?> type, Level level, BlockPos pos) {}

    default void updateConnection(Set<Direction> connectedSides) {}

    /**
     * @return true if a node with this extradata can connect to a node with the otherData as extradata. this.canConnectTo(other) and other.canConnectTo(this) should be the same
     */
    default boolean canConnectTo(T otherData) {
        return true;
    }

    /**
     * This method is called after otherData is integrated into this network. change this or otherData accordingly if you want shared state for all nodes in a graph (like fluids in fluid conduits)
     *
     * @param otherData
     */
    default void onConnectTo(T otherData) {
    }

    // endregion

    // region Client Sync

    /**
     * @return true if this needs to be synced to the client. if this returns true, deepCopy has to be overriden, to create a copy
     */
    default boolean syncDataToClient() {
        return false;
    }

    /**
     * @return synced renderdata
     */
    default CompoundTag serializeRenderNBT() {
        return new CompoundTag();
    }

    /**
     * @return synced guidata
     */
    default CompoundTag serializeGuiNBT() {
        return new CompoundTag();
    }

    // endregion

    @UseOnly(LogicalSide.CLIENT)
    default T deepCopy() {
        return cast();
    }

    default <Z extends ConduitData<Z>> Z cast() {
        return (Z) this;
    }

    default <Z extends ConduitData<Z>> Z castTo(Class<Z> clazz) {
        return cast();
    }

    /**
     * default impl for stuff that don't need an impl
     */
    class EmptyConduitData implements ConduitData<EmptyConduitData> {

        @Override
        public CompoundTag serializeNBT() {
            return new CompoundTag();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
        }
    }
}
