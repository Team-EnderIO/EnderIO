package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.SimpleConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record AE2Conduit(
    ResourceLocation texture,
    Component description,
    boolean isDense
) implements SimpleConduit<AE2Conduit, AE2InWorldConduitNodeHost> {

    public static MapCodec<AE2Conduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(AE2Conduit::texture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(AE2Conduit::description),
            Codec.BOOL.fieldOf("is_dense").forGetter(AE2Conduit::isDense)
        ).apply(builder, AE2Conduit::new)
    );

    @Override
    public ConduitType<AE2Conduit> type() {
        return AE2Integration.AE2_CONDUIT.get();
    }

    @Override
    public ConduitTicker<AE2Conduit, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> getTicker() {
        return Ticker.INSTANCE;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MenuData.INSTANCE;
    }

    @Override
    public AE2InWorldConduitNodeHost createConduitData(Level level, BlockPos pos) {
        // Ew.
        return AE2InWorldConduitNodeHost.create(level.registryAccess().registryOrThrow(EnderIORegistries.Keys.CONDUIT).wrapAsHolder(this));
    }

    @Override
    public boolean hasConnectionDelay() {
        return true;
    }

    @Override
    public boolean canConnectTo(Holder<Conduit<?, ?, ?>> other) {
        return other.value().type() == type();
    }

    @Override
    public void onCreated(AE2InWorldConduitNodeHost data, Level level, BlockPos pos, @Nullable Player player) {
        if (data.getMainNode() == null) {
            data.initMainNode();
        }

        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode.isReady()) {
            return;
        }

        if (player != null) {
            mainNode.setOwningPlayer(player);
        }

        GridHelper.onFirstTick(level.getBlockEntity(pos), blockEntity -> mainNode.create(level, pos));
    }

    @Override
    public void onRemoved(AE2InWorldConduitNodeHost data, Level level, BlockPos pos) {
        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode != null) {
            mainNode.destroy();
            data.clearMainNode();
        }
    }

    @Override
    public void onConnectionsUpdated(AE2InWorldConduitNodeHost data, Level level, BlockPos pos,
        Set<Direction> connectedSides) {
        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode != null) {
            mainNode.setExposedOnSides(connectedSides);
        }
    }

    @Override
    public <K> @Nullable K proxyCapability(BlockCapability<K, Direction> capability, ConduitNode<ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> node, Level level, BlockPos pos,
        @Nullable Direction direction, ConduitNode.@Nullable IOState state) {

        if (capability == AE2Integration.IN_WORLD_GRID_NODE_HOST) {
            return (K) node.getConduitData();
        }

        return null;
    }

    @Override
    public int compareTo(@NotNull AE2Conduit o) {
        if (isDense() && !o.isDense()) {
            return 1;
        } else if (!isDense() && o.isDense()) {
            return -1;
        }

        return 0;
    }

    private static final class MenuData implements ConduitMenuData {

        private static final MenuData INSTANCE = new MenuData();

        @Override
        public boolean hasFilterInsert() {
            return false;
        }

        @Override
        public boolean hasFilterExtract() {
            return false;
        }

        @Override
        public boolean hasUpgrade() {
            return false;
        }

        @Override
        public boolean showBarSeparator() {
            return false;
        }

        @Override
        public boolean showBothEnable() {
            return false;
        }

        @Override
        public boolean showColorInsert() {
            return false;
        }

        @Override
        public boolean showColorExtract() {
            return false;
        }

        @Override
        public boolean showRedstoneExtract() {
            return false;
        }
    }

    private static final class Ticker implements ConduitTicker<AE2Conduit, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> {

        private static final Ticker INSTANCE = new Ticker();

        @Override
        public void tickGraph(ServerLevel level, AE2Conduit type,
            ConduitNetwork<ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> graph, ColoredRedstoneProvider coloredRedstoneProvider) {
            //ae2 graphs don't actually do anything, that's all done by ae2
        }

        @Override
        public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
            return GridHelper.getExposedNode(level, conduitPos.relative(direction), direction.getOpposite()) != null;
        }
    }
}
