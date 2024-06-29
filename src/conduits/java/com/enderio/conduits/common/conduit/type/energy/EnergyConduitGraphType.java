package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitGraphContext;
import com.enderio.api.conduit.ConduitGraphType;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyConduitGraphType implements ConduitGraphType<EnergyConduitOptions, EnergyConduitGraphContext, ConduitData.EmptyConduitData> {

    private static final EnergyConduitTicker TICKER = new EnergyConduitTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    @Override
    public EnergyConduitTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData(EnergyConduitOptions options) {
        return MENU_DATA;
    }

    @Nullable
    @Override
    public EnergyConduitGraphContext createGraphContext(EnergyConduitOptions options) {
        return new EnergyConduitGraphContext();
    }

    @Override
    public ConduitData.EmptyConduitData createConduitData(EnergyConduitOptions options, Level level, BlockPos pos) {
        return ConduitData.EMPTY;
    }

    @Override
    public boolean canBeInSameBundle(EnergyConduitOptions energyConduitOptions, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.graphType() != this) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeReplacedBy(EnergyConduitOptions energyConduitOptions, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.graphType() != this) {
            return false;
        }

        if (conduitType.options() instanceof EnergyConduitOptions otherOptions) {
            return energyConduitOptions.transferRate() <= otherOptions.transferRate();
        }

        return false;
    }

    @Override
    public <K> @Nullable K proxyCapability(EnergyConduitOptions options, BlockCapability<K, Direction> capability,
        ConduitGraph<EnergyConduitGraphContext, ConduitData.EmptyConduitData> graph, ConduitData.EmptyConduitData conduitData, Level level, BlockPos pos,
        @Nullable Direction direction, @Nullable ConduitNode.IOState state) {
        if (Capabilities.EnergyStorage.BLOCK == capability
            && (state == null || state.isExtract())
            && (direction == null || !level.getBlockState(pos.relative(direction)).is(ConduitTags.Blocks.ENERGY_CABLE))) {
            //noinspection unchecked
            return (K) new EnergyConduitStorage(options, graph);
        }
        return null;
    }

    @Override
    public void onRemoved(EnergyConduitOptions options, ConduitData.EmptyConduitData data, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public ConduitConnectionData getDefaultConnection(EnergyConduitOptions options, Level level, BlockPos pos, Direction direction) {
        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
        if (capability != null) {
            return new ConduitConnectionData(capability.canReceive(), capability.canExtract(), RedstoneControl.ALWAYS_ACTIVE);
        }

        return ConduitGraphType.super.getDefaultConnection(options, level, pos, direction);
    }
}
