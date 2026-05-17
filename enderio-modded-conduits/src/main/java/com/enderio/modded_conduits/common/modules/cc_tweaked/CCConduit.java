package com.enderio.modded_conduits.common.modules.cc_tweaked;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.ConnectionReader;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.network.wired.WiredElementCapability;
import dan200.computercraft.api.network.wired.WiredNode;
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

public record CCConduit(Identifier texture, Component description)
        implements Conduit<CCConduit, CCConduitConnectionConfig> {

    public static final MapCodec<CCConduit> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(Identifier.CODEC.fieldOf("texture").forGetter(CCConduit::texture),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(CCConduit::description))
            .apply(builder, CCConduit::new));

    @Override
    public ConduitType<CCConduit, CCConduitConnectionConfig> type() {
        return CCConduitCommonModule.CC_CONDUIT_TYPE.get();
    }

    @Override
    public boolean hasMenu() {
        return false;
    }

    @Override
    public boolean canConnectToConduit(CCConduit other) {
        return true;
    }

    @Override
    public boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        return capabilityAccessor.getSidedCapability(WiredElementCapability.get(), direction) != null;
    }

    @Override
    public void onCreated(ConduitNode node, Level level, BlockPos pos, @Nullable Player player) {
        var data = node.getOrCreateNodeData(CCConduitNodeData.TYPE);
        data.initialize(level, pos);
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        var data = node.getOrCreateNodeData(CCConduitNodeData.TYPE);
        // Remove this node from the CC:Tweaked wired network.
        // This will disconnect from all neighbors and trigger network split detection if needed.
        data.getNode().remove();
    }

    @Override
    public void onConnectionsUpdated(ConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        var data = node.getOrCreateNodeData(CCConduitNodeData.TYPE);
        if (!data.isInitialized()) {
            data.initialize(level, pos);
        }

        WiredNode myNode = data.getNode();

        for (Direction direction : Direction.values()) {
            WiredElement neighborElement = getNeighborWiredElement(level, pos, direction);
            if (neighborElement == null) {
                continue;
            }

            WiredNode neighborNode = neighborElement.getNode();
            if (connectedSides.contains(direction)) {
                myNode.connectTo(neighborNode);
            } else {
                myNode.disconnectFrom(neighborNode);
            }
        }
    }

    @Override
    public @Nullable <TCapability, TContext> TCapability proxyCapability(Level level, ConnectionReader connectionReader, @Nullable ConduitNode node,
            BlockCapability<TCapability, TContext> capability, @Nullable TContext tContext) {
        if (node == null) {
            return null;
        }

        if (capability == WiredElementCapability.get()) {
            var data = node.getOrCreateNodeData(CCConduitNodeData.TYPE);
            if (!data.isInitialized()) {
                data.initialize(level, node.pos());
            }
            // noinspection unchecked
            return (TCapability) data.getElement();
        }

        return null;
    }

    @Override
    public int compareTo(@NonNull CCConduit o) {
        return 0;
    }

    @Nullable
    private static WiredElement getNeighborWiredElement(Level level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        if (!level.isLoaded(neighborPos)) {
            return null;
        }
        return level.getCapability(WiredElementCapability.get(), neighborPos, direction.getOpposite());
    }
}
