package com.enderio.modded_conduits.common.modules.appeng;

import appeng.api.AECapabilities;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AEColor;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.ConnectionReader;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Set;

public record MEConduit(Identifier texture, Component description, AEColor color, boolean isDense)
        implements Conduit<MEConduit, MEConduitConnectionConfig> {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final MapCodec<MEConduit> CODEC = RecordCodecBuilder
            .mapCodec(builder -> builder
                    .group(Identifier.CODEC.fieldOf("texture").forGetter(MEConduit::texture),
                            ComponentSerialization.CODEC.fieldOf("description").forGetter(MEConduit::description),
                            AEColor.CODEC.optionalFieldOf("color", AEColor.TRANSPARENT).forGetter(MEConduit::color),
                            Codec.BOOL.fieldOf("is_dense").forGetter(MEConduit::isDense))
                    .apply(builder, MEConduit::new));

    @Override
    public ConduitType<MEConduit, MEConduitConnectionConfig> type() {
        return AE2ConduitsModule.AE2_CONDUIT.get();
    }

    @Override
    public boolean hasMenu() {
        return false;
    }

    @Override
    public boolean canReplaceConduit(MEConduit otherConduit) {
        return compareTo(otherConduit) > 0;
    }

    @Override
    public boolean canConnectToConduit(MEConduit other) {
        return other.color == color;
    }

    @Override
    public boolean shouldCheckConnectionsOnNeighborChange() {
        return false;
    }

    @Override
    public boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        return GridHelper.getExposedNode(level, conduitPos.relative(direction), direction.getOpposite()) != null;
    }

    @Override
    public void onCreated(ConduitNode node, Level level, BlockPos pos, @Nullable Player player) {
        var data = node.getOrCreateNodeData(MEConduitNodeData.TYPE);
        data.init(this, level, pos, player);
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        // Do not create new data if we're removing.
        var data = node.getNodeData(MEConduitNodeData.TYPE);
        if (data != null) {
            data.destroy(level, pos);
        }
    }

    @Override
    public void onConnectionsUpdated(ConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        var data = node.getOrCreateNodeData(MEConduitNodeData.TYPE);
        data.setExposedSides(connectedSides);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <TCapability, TContext> TCapability proxyCapability(Level level, ConnectionReader connectionReader, @Nullable ConduitNode node,
        BlockCapability<TCapability, TContext> capability, @Nullable TContext tContext) {
        if (node != null && capability == AECapabilities.IN_WORLD_GRID_NODE_HOST) {
            return (TCapability) node.getOrCreateNodeData(MEConduitNodeData.TYPE);
        }

        return null;
    }

    @Override
    public int compareTo(@NotNull MEConduit o) {
        if (isDense() && !o.isDense()) {
            return 1;
        } else if (!isDense() && o.isDense()) {
            return -1;
        }

        return 0;
    }
}
