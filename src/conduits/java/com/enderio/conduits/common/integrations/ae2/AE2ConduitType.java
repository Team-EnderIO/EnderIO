package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IInWorldGridNodeHost;
import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.integrations.Integrations;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AE2ConduitType extends TieredConduit<AE2InWorldConduitNodeHost> {

    private final boolean dense;

    public AE2ConduitType(boolean dense) {
        super(EnderIO.loc("block/conduit/" + (dense ? "dense_me" : "me")), new ResourceLocation("ae2", "me_cable"), dense ? 32 : 8,
            EnderConduitTypes.ICON_TEXTURE, new Vector2i(0, dense ? 72 : 48));
        this.dense = dense;
    }

    @Override
    public IConduitTicker getTicker() {
        return Ticker.INSTANCE;
    }

    @Override
    public IConduitMenuData getMenuData() {
        return ConduitMenuData.INSTANCE;
    }

    @Override
    public AE2InWorldConduitNodeHost createExtendedConduitData(Level level, BlockPos pos) {
        return new AE2InWorldConduitNodeHost(this);
    }

    @Override
    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, AE2InWorldConduitNodeHost extendedConduitData, @Nullable Direction direction) {
        if (getCapability() == cap) {
            return Optional.of(extendedConduitData.selfCap.cast());
        }
        return Optional.empty();
    }

    @Override
    public Item getConduitItem() {
        if (isDense())
            return AE2Integration.DENSE_ITEM.get();
        return AE2Integration.NORMAL_ITEM.get();
    }

    public boolean isDense() {
        return dense;
    }

    protected final Capability<IInWorldGridNodeHost> getCapability() {
        return Integrations.ae2Integration.expectPresent().getInWorldGridNodeHost();
    }

    private static final class ConduitMenuData implements IConduitMenuData {

        private static final IConduitMenuData INSTANCE = new ConduitMenuData();

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

    private static final class Ticker implements IConduitTicker {

        private static final Ticker INSTANCE = new Ticker();
        @Override
        public void tickGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
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
        public boolean canConnectTo(IConduitType<?> thisType, IConduitType<?> other) {
            return other instanceof AE2ConduitType;
        }
    }
}
