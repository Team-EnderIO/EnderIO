package com.enderio.api.conduit;

import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.RedstoneControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ConduitGraphType<TOptions, TContext extends ConduitGraphContext<TContext>, TData extends ConduitData<TData>> {
    /**
     * Get the ticker for this conduit graph type.
     * @apiNote The ticker should never change, it can use the options to determine behaviour in its implementation.
     */
    ConduitTicker<TOptions, TContext, TData> getTicker();
    ConduitMenuData getMenuData(TOptions options);

    // TODO: Could expose the graph to this method too...
    @Nullable
    TContext createGraphContext(TOptions options);
    TData createConduitData(TOptions options, Level level, BlockPos pos);

    // TODO: This might be able to take the options of the other conduit type.
    //       Checking against other graphs is likely unnecessary.
    default boolean canBeInSameBundle(TOptions options, ConduitType<?, ?, ?> conduitType) {
        return true;
    }

    default boolean canBeReplacedBy(TOptions options, ConduitType<?, ?, ?> conduitType) {
        return true;
    }

    default boolean canApplyUpgrade(TOptions options, SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return false;
    }

    default boolean canApplyFilter(TOptions options, SlotType slotType, ResourceFilter resourceFilter) {
        return false;
    }

    // region Events

    default void onCreated(TOptions options, TData data, Level level, BlockPos pos, @Nullable Player player) {
    }

    default void onRemoved(TOptions options, TData data, Level level, BlockPos pos) {
    }

    default void onConnectionsUpdated(TOptions options, TData data, Level level, BlockPos pos, Set<Direction> connectedSides) {
    }

    // endregion

    @Nullable
    default <K> K proxyCapability(TOptions options, BlockCapability<K, Direction> capability, ConduitGraph<TContext, TData> graph, TData conduitData, Level level, BlockPos pos,
        @Nullable Direction direction, @Nullable ConduitNode.IOState state) {
        return null;
    }

    default ConduitConnectionData getDefaultConnection(TOptions options, Level level, BlockPos pos, Direction direction) {
        return new ConduitConnectionData(false, true, RedstoneControl.NEVER_ACTIVE);
    }

    record ConduitConnectionData(boolean isInsert, boolean isExtract, RedstoneControl control) {}
}
