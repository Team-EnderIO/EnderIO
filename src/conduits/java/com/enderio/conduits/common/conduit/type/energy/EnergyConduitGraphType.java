package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitGraphContext;
import com.enderio.api.conduit.ConduitGraphType;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNode;
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

public class EnergyConduitGraphType implements ConduitGraphType<Void, ConduitGraphContext.Dummy, EnergyConduitData> {

    private static final EnergyConduitTicker TICKER = new EnergyConduitTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    @Override
    public ConduitTicker<Void, ConduitGraphContext.Dummy, EnergyConduitData> getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData(Void unused) {
        return MENU_DATA;
    }

    @Nullable
    @Override
    public ConduitGraphContext.Dummy createGraphContext(Void unused) {
        return null;
    }

    @Override
    public EnergyConduitData createConduitData(Void unused, Level level, BlockPos pos) {
        return new EnergyConduitData();
    }

    @Override
    public <K> @Nullable K proxyCapability(Void unused, BlockCapability<K, Direction> capability, EnergyConduitData conduitData, Level level, BlockPos pos,
        @Nullable Direction direction, ConduitNode.@Nullable IOState state) {
        if (Capabilities.EnergyStorage.BLOCK == capability
            && (state == null || state.isExtract())
            && (direction == null || !level.getBlockState(pos.relative(direction)).is(ConduitTags.Blocks.ENERGY_CABLE))) {
            //noinspection unchecked
            return (K) conduitData.getSelfCap();

        }
        return null;
    }

    @Override
    public void onRemoved(Void unused, EnergyConduitData data, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public ConduitConnectionData getDefaultConnection(Void unused, Level level, BlockPos pos, Direction direction) {
        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
        if (capability != null) {
            return new ConduitConnectionData(capability.canReceive(), capability.canExtract(), RedstoneControl.ALWAYS_ACTIVE);
        }

        return ConduitGraphType.super.getDefaultConnection(unused, level, pos, direction);
    }
}
