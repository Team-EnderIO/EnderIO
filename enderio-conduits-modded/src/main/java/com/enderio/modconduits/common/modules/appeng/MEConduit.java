package com.enderio.modconduits.common.modules.appeng;

import appeng.api.AECapabilities;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AEColor;
import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.node.IConduitNode;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record MEConduit(ResourceLocation texture, Component description, AEColor color, boolean isDense)
        implements Conduit<MEConduit, MEConduitConnectionConfig> {

    public static final MapCodec<MEConduit> CODEC = RecordCodecBuilder
            .mapCodec(builder -> builder
                    .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(MEConduit::texture),
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
    public boolean hasConnectionDelay() {
        return true;
    }

    @Override
    public boolean canConnectToConduit(Holder<Conduit<?, ?>> other) {
        return other.value().type() == type() && other.value() instanceof MEConduit otherConduit
                && otherConduit.color == color;
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        return GridHelper.getExposedNode(level, conduitPos.relative(direction), direction.getOpposite()) != null;
    }

    @Override
    public void onCreated(IConduitNode node, Level level, BlockPos pos, @Nullable Player player) {
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
    public void onRemoved(IConduitNode node, Level level, BlockPos pos) {
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
                .registryOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT)
                .wrapAsHolder(this);

        mainNode = GridHelper.createManagedNode(nodeHost, GridNodeListener.INSTANCE)
                .setVisualRepresentation(ConduitBlockItem.getStackFor(asHolder, 1))
                .setInWorldNode(true)
                .setTagName("conduit")
                .setGridColor(color);

        mainNode.setIdlePowerUsage(isDense() ? 0.4d : 0.1d);

        if (isDense()) {
            mainNode.setFlags(GridFlags.DENSE_CAPACITY);
        }

        nodeHost.setMainNode(mainNode, isDense());

        // Load any saved data
        nodeHost.loadMainNode();
    }

    @Override
    public void onConnectionsUpdated(IConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        var data = node.getOrCreateNodeData(MEConduitNodeData.TYPE);
        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode != null) {
            mainNode.setExposedOnSides(connectedSides);
        }
    }

    @Override
    public <TCapability, TContext> @Nullable TCapability proxyCapability(Level level, @Nullable IConduitNode node,
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
    public int compareTo(@NotNull MEConduit o) {
        if (isDense() && !o.isDense()) {
            return 1;
        } else if (!isDense() && o.isDense()) {
            return -1;
        }

        return 0;
    }
}
