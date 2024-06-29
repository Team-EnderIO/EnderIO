package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkType;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.RedstoneControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EnergyConduitNetworkType implements ConduitNetworkType<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> {

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
    public EnergyConduitNetworkContext createNetworkContext(ConduitType<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> type,
        ConduitNetwork<EnergyConduitNetworkContext, ConduitData.EmptyConduitData> network) {
        return new EnergyConduitNetworkContext();
    }

    @Override
    public ConduitData.EmptyConduitData createConduitData(ConduitType<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> type, Level level, BlockPos pos) {
        return ConduitData.EMPTY;
    }

    @Override
    public boolean canBeInSameBundle(EnergyConduitOptions energyConduitOptions, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.networkType() != this) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeReplacedBy(EnergyConduitOptions energyConduitOptions, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.networkType() != this) {
            return false;
        }

        if (conduitType.options() instanceof EnergyConduitOptions otherOptions) {
            return energyConduitOptions.transferLimit() <= otherOptions.transferLimit();
        }

        return false;
    }

    @Override
    public <K> @Nullable K proxyCapability(ConduitType<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> type,
        BlockCapability<K, Direction> capability, ConduitNode<EnergyConduitNetworkContext, ConduitData.EmptyConduitData> node,
        Level level, BlockPos pos, @Nullable Direction direction, @Nullable ConduitNode.IOState state) {

        if (Capabilities.EnergyStorage.BLOCK == capability && (state == null || state.isExtract())) {
            //noinspection unchecked
            return (K) new EnergyConduitStorage(type.options(), node);
        }

        return null;
    }

    @Override
    public Set<BlockCapability<?, Direction>> getExposedCapabilities() {
        return Set.of(Capabilities.EnergyStorage.BLOCK);
    }

    @Override
    public void onRemoved(EnergyConduitOptions options, ConduitData.EmptyConduitData data, Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public ConduitConnectionData getDefaultConnection(EnergyConduitOptions options, Level level, BlockPos pos, Direction direction) {
        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
        if (capability != null) {
            // Always default to both directions.
            return new ConduitConnectionData(true, true, RedstoneControl.ALWAYS_ACTIVE);
        }

        return ConduitNetworkType.super.getDefaultConnection(options, level, pos, direction);
    }

    @Override
    public int compare(EnergyConduitOptions o1, EnergyConduitOptions o2) {
        if (o1.transferLimit() < o2.transferLimit()) {
            return -1;
        } else if (o1.transferLimit() > o2.transferLimit()) {
            return 1;
        }

        return 0;
    }
}
