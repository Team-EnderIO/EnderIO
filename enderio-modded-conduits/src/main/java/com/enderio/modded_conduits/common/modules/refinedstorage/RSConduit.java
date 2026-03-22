package com.enderio.modded_conduits.common.modules.refinedstorage;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.refinedmods.refinedstorage.neoforge.RefinedStorageNeoForgeApiImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public record RSConduit(Identifier texture, Component description)
        implements Conduit<RSConduit, RSConduitConnectionConfig> {
    public static final MapCodec<RSConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(Identifier.CODEC.fieldOf("texture").forGetter(RSConduit::texture),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(RSConduit::description))
            .apply(builder, RSConduit::new));

    @Override
    public ConduitType<RSConduit, RSConduitConnectionConfig> type() {
        return RefinedStorageCommonModule.RS_CONDUIT.get();
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
    public void onCreated(ConduitNode node, Level level, BlockPos pos, @Nullable Player player) {
        var data = node.getOrCreateNodeData(RSConduitNodeData.TYPE);
        data.initialize(node, level, pos);
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        var data = node.getOrCreateNodeData(RSConduitNodeData.TYPE);
        data.remove(level);
    }

    @Override
    public void onConnectionsUpdated(ConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        var data = node.getOrCreateNodeData(RSConduitNodeData.TYPE);
        data.update(level, connectedSides);
    }

    @Override
    public <TCapability, TContext> @Nullable TCapability proxyCapability(Level level, @Nullable ConduitNode node,
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
    public int compareTo(@NonNull RSConduit o) {
        return 0;
    }
}
