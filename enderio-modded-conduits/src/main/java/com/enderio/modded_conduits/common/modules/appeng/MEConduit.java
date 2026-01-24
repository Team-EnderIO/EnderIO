package com.enderio.modded_conduits.common.modules.appeng;

import appeng.api.AECapabilities;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AEColor;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.ticker.ConduitTicker;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduit;
import com.enderio.modded_conduits.config.ModdedConduitsConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public record MEConduit(Identifier texture, Component description, AEColor color, boolean isDense)
        implements Conduit<MEConduit, MEConduitConnectionConfig> {

    public static final MapCodec<MEConduit> CODEC = RecordCodecBuilder
            .mapCodec(builder -> builder
                    .group(Identifier.CODEC.fieldOf("texture").forGetter(MEConduit::texture),
                            ComponentSerialization.CODEC.fieldOf("description").forGetter(MEConduit::description),
                            AEColor.CODEC.optionalFieldOf("color", AEColor.TRANSPARENT).forGetter(MEConduit::color),
                            Codec.BOOL.fieldOf("is_dense").forGetter(MEConduit::isDense))
                    .apply(builder, MEConduit::new));

    @Override
    public ConduitType<MEConduit> type() {
        return AE2ConduitsModule.AE2_CONDUIT.get();
    }

    @Override
    public ConnectionConfigType<MEConduitConnectionConfig> connectionConfigType() {
        return MEConduitConnectionConfig.TYPE;
    }

    @Override
    public @Nullable ConduitTicker<MEConduit> ticker() {
        return null;
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
    public boolean hasConnectionDelay() {
        return true;
    }

    @Override
    public boolean canConnectToConduit(MEConduit other) {
        return other.color == color;
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        return GridHelper.getExposedNode(level, conduitPos.relative(direction), direction.getOpposite()) != null;
    }

    @Override
    public void onCreated(ConduitNode node, Level level, BlockPos pos, @Nullable Player player) {
        var data = node.getOrCreateNodeData(MEConduitNodeData.TYPE);

        if (data.getMainNode() == null) {
            initMainNode(level, data);
        }

        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode.isReady()) {
            return;
        }

        if (player != null) {
            mainNode.setOwningPlayer(player);
        }

        GridHelper.onFirstTick(level.getBlockEntity(pos), blockEntity -> {
            if (!mainNode.isReady()) {
                mainNode.create(level, pos);
            }
        });
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        var data = node.getOrCreateNodeData(MEConduitNodeData.TYPE);
        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode != null) {
            mainNode.destroy();
            data.clearMainNode();
        }
    }

    private void initMainNode(Level level, MEConduitNodeData nodeHost) {
        var mainNode = nodeHost.getMainNode();
        if (mainNode != null) {
            throw new UnsupportedOperationException("mainNode is already initialized");
        }

        Holder<Conduit<?, ?>> asHolder = level.registryAccess()
                .registryOrThrow(EnderIORegistries.Keys.CONDUIT)
                .wrapAsHolder(this);

        mainNode = GridHelper.createManagedNode(nodeHost, GridNodeListener.INSTANCE)
                .setVisualRepresentation(ConduitBlockItem.getStackFor(asHolder, 1))
                .setInWorldNode(true)
                .setTagName("conduit")
                .setGridColor(color);

        mainNode.setIdlePowerUsage(isDense() 
            ? ModdedConduitsConfig.COMMON.AE2.DENSE_ME_POWER_USAGE_PER_TICK.get()
            : ModdedConduitsConfig.COMMON.AE2.NORMAL_ME_POWER_USAGE_PER_TICK.get());

        if (isDense()) {
            mainNode.setFlags(GridFlags.DENSE_CAPACITY);
        }

        nodeHost.setMainNode(mainNode, isDense());

        // Load any saved data
        nodeHost.loadMainNode();
    }

    @Override
    public void onConnectionsUpdated(ConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        var data = node.getOrCreateNodeData(MEConduitNodeData.TYPE);
        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode != null) {
            mainNode.setExposedOnSides(connectedSides);
        }
    }

    @Override
    public <TCapability, TContext> @Nullable TCapability proxyCapability(Level level, @Nullable ConduitNode node,
            BlockCapability<TCapability, TContext> capability, @Nullable TContext tContext) {
        if (node != null && capability == AECapabilities.IN_WORLD_GRID_NODE_HOST) {
            // noinspection unchecked
            return (TCapability) node.getOrCreateNodeData(MEConduitNodeData.TYPE);
        }

        return null;
    }

    @Override
    public MEConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new MEConduitConnectionConfig(isInsert);
    }

    @Override
    public int compareTo(@NonNull MEConduit o) {
        if (isDense() && !o.isDense()) {
            return 1;
        } else if (!isDense() && o.isDense()) {
            return -1;
        }

        return 0;
    }
}
