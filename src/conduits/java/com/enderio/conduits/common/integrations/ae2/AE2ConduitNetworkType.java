package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AE2ConduitNetworkType implements ConduitNetworkType<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> {
    @Override
    public ConduitTicker<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> getTicker() {
        return Ticker.INSTANCE;
    }

    @Override
    public ConduitMenuData getMenuData(AE2ConduitOptions options) {
        return MenuData.INSTANCE;
    }

    @Override
    public @Nullable ConduitNetworkContext.Dummy createNetworkContext(
        ConduitType<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> type,
        ConduitNetwork<ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> network) {
        return null;
    }

    @Override
    public AE2InWorldConduitNodeHost createConduitData(ConduitType<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> type,
        Level level, BlockPos pos) {
        return AE2InWorldConduitNodeHost.create(type);
    }

    @Override
    public void onCreated(AE2ConduitOptions ae2ConduitOptions, AE2InWorldConduitNodeHost data, Level level, BlockPos pos, @Nullable Player player) {
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
    public void onRemoved(AE2ConduitOptions ae2ConduitOptions, AE2InWorldConduitNodeHost data, Level level, BlockPos pos) {
        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode != null) {
            mainNode.destroy();
            data.clearMainNode();
        }
    }

    @Override
    public void onConnectionsUpdated(AE2ConduitOptions ae2ConduitOptions, AE2InWorldConduitNodeHost data, Level level, BlockPos pos,
        Set<Direction> connectedSides) {
        IManagedGridNode mainNode = data.getMainNode();
        if (mainNode != null) {
            mainNode.setExposedOnSides(connectedSides);
        }
    }

    @Override
    public <K> @Nullable K proxyCapability(ConduitType<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> type,
        BlockCapability<K, Direction> capability, ConduitNode<ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> node, Level level, BlockPos pos,
        @Nullable Direction direction, ConduitNode.@Nullable IOState state) {

        if (capability == AE2Integration.IN_WORLD_GRID_NODE_HOST) {
            return (K) node.getConduitData();
        }

        return null;
    }

    @Override
    public Set<BlockCapability<?, Direction>> getExposedCapabilities() {
        return Set.of(AE2Integration.IN_WORLD_GRID_NODE_HOST);
    }

    @Override
    public int compare(AE2ConduitOptions o1, AE2ConduitOptions o2) {
        if (o1.isDense() && !o2.isDense()) {
            return 1;
        } else if (!o1.isDense() && o2.isDense()) {
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

    private static final class Ticker implements ConduitTicker<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> {

        private static final Ticker INSTANCE = new Ticker();

        @Override
        public void tickGraph(ServerLevel level, ConduitType<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> type,
            ConduitNetwork<ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost> graph, ColoredRedstoneProvider coloredRedstoneProvider) {
            //ae2 graphs don't actually do anything, that's all done by ae2
        }

        @Override
        public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
            return GridHelper.getExposedNode(level, conduitPos.relative(direction), direction.getOpposite()) != null;
        }

        @Override
        public boolean hasConnectionDelay() {
            return true;
        }

        @Override
        public boolean canConnectTo(ConduitType<?, ?, ?> thisType, ConduitType<?, ?, ?> other) {
            return thisType.networkType() == other.networkType();
        }
    }
}
