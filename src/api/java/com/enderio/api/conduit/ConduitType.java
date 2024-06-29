package com.enderio.api.conduit;

import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.registry.EnderIORegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record ConduitType<TOptions, TContext extends ConduitNetworkContext<TContext>, TData extends ConduitData<TData>>(
    ConduitNetworkType<TOptions, TContext, TData> graphType,
    TOptions options
) {
    public ConduitTicker<TOptions, TContext, TData> getTicker() {
        return graphType.getTicker();
    }

    public ConduitMenuData getMenuData() {
        return graphType.getMenuData(options);
    }

    @Nullable
    public TContext createGraphContext(ConduitNetwork<TContext, TData> network) {
        return graphType.createNetworkContext(this, network);
    }

    public TData createConduitData(Level level, BlockPos pos) {
        return graphType.createConduitData(this, level, pos);
    }

    public boolean canBeInSameBundle(ConduitType<?, ?, ?> conduitType) {
        return graphType.canBeInSameBundle(options, conduitType);
    }

    public boolean canBeReplacedBy(ConduitType<?, ?, ?> conduitType) {
        return graphType.canBeReplacedBy(options, conduitType);
    }

    public boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return graphType.canApplyUpgrade(options, slotType, conduitUpgrade);
    }

    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return graphType.canApplyFilter(options, slotType, resourceFilter);
    }

    public void onCreated(TData data, Level level, BlockPos pos, @Nullable Player player) {
        graphType.onCreated(options, data, level, pos, player);
    }

    public void onRemoved(TData data, Level level, BlockPos pos) {
        graphType.onRemoved(options, data, level, pos);
    }

    public void onConnectionsUpdated(TData data, Level level, BlockPos pos, Set<Direction> connectedSides) {
        graphType.onConnectionsUpdated(options, data, level, pos, connectedSides);
    }

    @Nullable
    public <K> K proxyCapability(BlockCapability<K, Direction> capability, ConduitNode<TContext, TData> node, Level level, BlockPos pos, @Nullable Direction direction, @Nullable ConduitNode.IOState state) {
        return graphType.proxyCapability(this, capability, node, level, pos, direction, state);
    }

    public ConduitNetworkType.ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        return graphType.getDefaultConnection(options, level, pos, direction);
    }

    @Nullable
    public static ResourceLocation getKey(ConduitType<?, ?, ?> conduitType) {
        return EnderIORegistries.CONDUIT_TYPES.getKey(conduitType);
    }
}
