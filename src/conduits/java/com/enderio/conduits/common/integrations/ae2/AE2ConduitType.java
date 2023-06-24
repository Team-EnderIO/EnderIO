package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.IInWorldGridNodeHost;
import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.Vector2i;
import com.enderio.api.conduit.TieredConduit;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AE2ConduitType extends TieredConduit<AE2InWorldConduitNodeHost> {

    private boolean dense;

    public AE2ConduitType(boolean dense) {
        super(EnderIO.loc("block/conduit/" + (dense ? "dense_me" : "me")), new ResourceLocation("ae2", "me_cable"), dense ? 32 : 8,
            EnderConduitTypes.ICON_TEXTURE, new Vector2i(0, dense ? 120 : 96));
        this.dense = dense;
    }

    @Override
    public IConduitTicker getTicker() {
        return new IConduitTicker() {
            @Override
            public void tickGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level) {}

            @Override
            public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
                BlockEntity blockEntity = level.getBlockEntity(conduitPos.relative(direction));
                if (blockEntity instanceof IInWorldGridNodeHost host && canConnectTo(host, direction))
                    return true;
                return blockEntity != null && blockEntity
                    .getCapability(getCapability(), direction.getOpposite())
                    .resolve()
                    .map(node -> canConnectTo(node, direction))
                    .orElse(false);
            }

            private static boolean canConnectTo(IInWorldGridNodeHost host, Direction direction) {
                return Optional.ofNullable(host.getGridNode(direction.getOpposite())).map(node -> node.isExposedOnSide(direction.getOpposite())).orElse(false);
            }

            @Override
            public boolean hasConnectionDelay() {
                return true;
            }
        };
    }

    @Override
    public IConduitMenuData getMenuData() {
        return ConduitMenuData.INSTANCE;
    }

    @Override
    public AE2InWorldConduitNodeHost createExtendedConduitData(Level level, BlockPos pos) {
        return new AE2InWorldConduitNodeHost(level, pos, this);
    }

    @Override
    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, AE2InWorldConduitNodeHost extendedConduitData, @Nullable Direction direction) {
        if (getCapability() == cap) {
            return Optional.of(LazyOptional.of(() -> extendedConduitData).cast());
        }
        return Optional.empty();
    }

    @Override
    public Item getConduitItem() {
        if (isDense())
            return Integrations.ae2Integration.expectPresent().DENSE_ITEM.get();
        return Integrations.ae2Integration.expectPresent().NORMAL_ITEM.get();
    }

    public boolean isDense() {
        return dense;
    }

    protected final Capability<IInWorldGridNodeHost> getCapability() {
        return Integrations.ae2Integration.expectPresent().getInWorldGridNodeHost();
    }

    private static final class ConduitMenuData implements IConduitMenuData {

        private static IConduitMenuData INSTANCE = new ConduitMenuData();

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
}
