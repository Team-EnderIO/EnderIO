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

public record ConduitType<TOptions, TContext extends ConduitGraphContext<TContext>, TData extends ConduitData<TData>>(
    ConduitGraphType<TOptions, TContext, TData> graphType,
    TOptions options
) {
    public ConduitTicker<TOptions, TContext, TData> getTicker() {
        return graphType.getTicker();
    }

    public ConduitMenuData getMenuData() {
        return graphType.getMenuData(options);
    }

    @Nullable
    public TContext createGraphContext() {
        return graphType.createGraphContext(options);
    }

    public TData createConduitData(Level level, BlockPos pos) {
        return graphType.createConduitData(options, level, pos);
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
    public <K> K proxyCapability(BlockCapability<K, Direction> capability, ConduitGraph<TContext, TData> graph, TData conduitData, Level level, BlockPos pos, @Nullable Direction direction, @Nullable ConduitNode.IOState state) {
        return graphType.proxyCapability(options, capability, graph, conduitData, level, pos, direction, state);
    }

    public ConduitGraphType.ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        return graphType.getDefaultConnection(options, level, pos, direction);
    }

    // TODO: 1.21: I want to have a single conduit item
    /**
     * Override this method if your conduit type and your conduit item registry name don't match
     * @return the conduit item that holds this type
     */
    public Item getConduitItem() {
        //ResourceLocation key = EnderIORegistries.CONDUIT_TYPES.getKey(this);
        //return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + "_conduit"));
        // TODO...
        return Items.GRANITE;
    }

    @Nullable
    public static ResourceLocation getKey(ConduitType<?, ?, ?> conduitType) {
        return EnderIORegistries.CONDUIT_TYPES.getKey(conduitType);
    }
}
