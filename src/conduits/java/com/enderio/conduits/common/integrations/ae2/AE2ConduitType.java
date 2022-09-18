package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.IInWorldGridNodeHost;
import com.enderio.EnderIO;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.conduits.common.blockentity.TieredConduit;
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
import java.util.function.Supplier;

public class AE2ConduitType extends TieredConduit<AE2InWorldConduitNodeHost> {

    private boolean dense;
    public AE2ConduitType(boolean dense, Supplier<Item> conduitItem) {
        super(EnderIO.loc("block/conduit/" + (dense ? "dense_me" : "me")), new ResourceLocation("ae2", "me_cable"), dense ? 32 : 8, conduitItem);
        this.dense = dense;
    }

    @Override
    public IConduitTicker getTicker() {
        return new IConduitTicker() {
            @Override
            public void tickGraph(Graph<Mergeable.Dummy> graph, ServerLevel level) { }

            @Override
            public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
                BlockEntity blockEntity = level.getBlockEntity(conduitPos.relative(direction));
                if (blockEntity instanceof IInWorldGridNodeHost)
                    return true;
                return blockEntity != null && blockEntity.getCapability(getCapability(), direction.getOpposite()).isPresent();
            }
        };
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

    public boolean isDense() {
        return dense;
    }

    protected final Capability<IInWorldGridNodeHost> getCapability() {
        return Integrations.ae2Integration.expectPresent().getInWorldGridNodeHost();
    }
}
