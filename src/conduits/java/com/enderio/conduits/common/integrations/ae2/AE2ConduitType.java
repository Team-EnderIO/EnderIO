package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IInWorldGridNodeHost;
import com.enderio.EnderIO;
import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.GraphAccessor;
import com.enderio.conduits.common.conduit.NodeIdentifier;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EIOConduitTypes;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AE2ConduitType extends TieredConduit<AE2InWorldConduitNodeHost> {

    private final boolean dense;

    public AE2ConduitType(boolean dense) {
        super(EnderIO.loc("block/conduit/" + (dense ? "dense_me" : "me")), new ResourceLocation("ae2", "me_cable"), dense ? 32 : 8,
            EIOConduitTypes.ICON_TEXTURE, new Vector2i(0, dense ? 72 : 48));
        this.dense = dense;
    }

    @Override
    public ConduitTicker<AE2InWorldConduitNodeHost> getTicker() {
        return Ticker.INSTANCE;
    }

    @Override
    public com.enderio.api.conduit.ConduitMenuData getMenuData() {
        return AE2ConduitType.ConduitMenuData.INSTANCE;
    }

    @Override
    public AE2InWorldConduitNodeHost createExtendedConduitData(Level level, BlockPos pos) {
        if (isDense()) {
            return new AE2InWorldConduitNodeHost.Dense();
        }

        return new AE2InWorldConduitNodeHost.Normal();
    }

    @Override
    public <K> Optional<K> proxyCapability(BlockCapability<K, Direction> cap, AE2InWorldConduitNodeHost extendedConduitData, Level level, BlockPos pos, @Nullable Direction direction, @Nullable NodeIdentifier.IOState state) {
        if (getCapability() == cap) {
            return (Optional<K>) Optional.of(extendedConduitData);
        }
        return Optional.empty();
    }

    @Override
    public Item getConduitItem() {
        if (isDense()) {
            return AE2Integration.DENSE_ITEM.get();
        }

        return AE2Integration.NORMAL_ITEM.get();
    }

    public boolean isDense() {
        return dense;
    }

    protected final BlockCapability<IInWorldGridNodeHost, Direction> getCapability() {
        return AE2Integration.IN_WORLD_GRID_NODE_HOST;
    }

    private static final class ConduitMenuData implements com.enderio.api.conduit.ConduitMenuData {

        private static final com.enderio.api.conduit.ConduitMenuData INSTANCE = new ConduitMenuData();

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
        public boolean showBarSeperator() {
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

    private static final class Ticker implements ConduitTicker<AE2InWorldConduitNodeHost> {

        private static final Ticker INSTANCE = new Ticker();
        @Override
        public void tickGraph(ServerLevel level, ConduitType<AE2InWorldConduitNodeHost> type, GraphAccessor<AE2InWorldConduitNodeHost> graph,
            ColoredRedstoneProvider coloredRedstoneProvider) {
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
        public boolean canConnectTo(ConduitType<?> thisType, ConduitType<?> other) {
            return other instanceof AE2ConduitType;
        }
    }
}
