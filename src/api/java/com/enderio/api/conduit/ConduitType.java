package com.enderio.api.conduit;

import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.registry.EnderIORegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public record ConduitType<TOptions, TContext extends ConduitNetworkContext<TContext>, TData extends ConduitData<TData>>(
    ConduitNetworkType<TOptions, TContext, TData> networkType,
    TOptions options
) implements Comparable<ConduitType<TOptions, TContext, TData>> {
    public ConduitTicker<TOptions, TContext, TData> getTicker() {
        return networkType.getTicker();
    }

    public ConduitMenuData getMenuData() {
        return networkType.getMenuData(options);
    }

    @Nullable
    public TContext createGraphContext(ConduitNetwork<TContext, TData> network) {
        return networkType.createNetworkContext(this, network);
    }

    public TData createConduitData(Level level, BlockPos pos) {
        return networkType.createConduitData(this, level, pos);
    }

    public boolean canBeInSameBundle(ConduitType<?, ?, ?> conduitType) {
        return networkType.canBeInSameBundle(options, conduitType);
    }

    public boolean canBeReplacedBy(ConduitType<?, ?, ?> conduitType) {
        return networkType.canBeReplacedBy(options, conduitType);
    }

    public boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return networkType.canApplyUpgrade(options, slotType, conduitUpgrade);
    }

    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return networkType.canApplyFilter(options, slotType, resourceFilter);
    }

    public void onCreated(TData data, Level level, BlockPos pos, @Nullable Player player) {
        networkType.onCreated(options, data, level, pos, player);
    }

    public void onRemoved(TData data, Level level, BlockPos pos) {
        networkType.onRemoved(options, data, level, pos);
    }

    public void onConnectionsUpdated(TData data, Level level, BlockPos pos, Set<Direction> connectedSides) {
        networkType.onConnectionsUpdated(options, data, level, pos, connectedSides);
    }

    @Nullable
    public <K> K proxyCapability(BlockCapability<K, Direction> capability, ConduitNode<TContext, TData> node, Level level, BlockPos pos, @Nullable Direction direction, @Nullable ConduitNode.IOState state) {
        return networkType.proxyCapability(this, capability, node, level, pos, direction, state);
    }

    public ConduitNetworkType.ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        return networkType.getDefaultConnection(options, level, pos, direction);
    }

    public List<Component> getHoverText(Item.TooltipContext context, TooltipFlag tooltipFlag) {
        return networkType.getHoverText(options, context, tooltipFlag);
    }

    @Nullable
    public static ResourceLocation getKey(ConduitType<?, ?, ?> conduitType) {
        return EnderIORegistries.CONDUIT_TYPES.getKey(conduitType);
    }

    @Override
    public int compareTo(@NotNull ConduitType<TOptions, TContext, TData> o) {
        return networkType().compare(options, o.options);
    }
}
