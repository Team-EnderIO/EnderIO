package com.enderio.modconduits.common.modules.refinedstorage;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.node.IConduitNode;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.refinedmods.refinedstorage.neoforge.RefinedStorageNeoForgeApiImpl;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RSConduit(ResourceLocation texture, Component description)
        implements Conduit<RSConduit, RSConduitConnectionConfig> {
    public static final MapCodec<RSConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(RSConduit::texture),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(RSConduit::description))
            .apply(builder, RSConduit::new));

    @Override
    public ConduitType<RSConduit> type() {
        return RefinedStorageCommonModule.RS_CONDUIT.get();
    }

    @Override
    public ConnectionConfigType<RSConduitConnectionConfig> connectionConfigType() {
        return RSConduitConnectionConfig.TYPE;
    }

    @Override
    public @Nullable ConduitTicker<RSConduit> ticker() {
        return null;
    }

    @Override
    public boolean hasMenu() {
        return false;
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        var cap = level.getCapability(
                RefinedStorageNeoForgeApiImpl.INSTANCE.getNetworkNodeContainerProviderCapability(),
                conduitPos.relative(direction), direction.getOpposite());
        if (cap != null) {
            for (var connection : cap.getContainers()) {
                if (connection.canAcceptIncomingConnection(direction.getOpposite(), level.getBlockState(conduitPos))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onCreated(IConduitNode node, Level level, BlockPos pos, @Nullable Player player) {
        var data = node.getOrCreateNodeData(RSConduitNodeData.TYPE);
        data.initialize(node, level, pos);
    }

    @Override
    public void onRemoved(IConduitNode node, Level level, BlockPos pos) {
        var data = node.getOrCreateNodeData(RSConduitNodeData.TYPE);
        data.remove(level);
    }

    @Override
    public void onConnectionsUpdated(IConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        var data = node.getOrCreateNodeData(RSConduitNodeData.TYPE);
        data.update(level, connectedSides);
    }

    @Override
    public <TCapability, TContext> @Nullable TCapability proxyCapability(Level level, @Nullable IConduitNode node,
            BlockCapability<TCapability, TContext> capability, @Nullable TContext tContext) {

        if (node != null && capability == RefinedStorageNeoForgeApiImpl.INSTANCE.getNetworkNodeContainerProviderCapability()) {
            var data = node.getOrCreateNodeData(RSConduitNodeData.TYPE);
            if (data.isAccessible()) {
                // noinspection unchecked
                return (TCapability) data.containerProvider;
            }
        }
        return null;
    }

    @Override
    public int compareTo(@NotNull RSConduit o) {
        return 0;
    }
}
