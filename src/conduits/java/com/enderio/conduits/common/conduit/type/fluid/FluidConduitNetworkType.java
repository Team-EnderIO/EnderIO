package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkType;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FluidConduitNetworkType implements ConduitNetworkType<FluidConduitOptions, ConduitNetworkContext.Dummy, FluidConduitData> {

    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
    private static final FluidConduitTicker TICKER = new FluidConduitTicker();

    @Override
    public ConduitTicker<FluidConduitOptions, ConduitNetworkContext.Dummy, FluidConduitData> getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData(FluidConduitOptions options) {
        return MENU_DATA;
    }

    @Override
    @Nullable
    public ConduitNetworkContext.Dummy createNetworkContext(ConduitType<FluidConduitOptions, ConduitNetworkContext.Dummy, FluidConduitData> type,
        ConduitNetwork<ConduitNetworkContext.Dummy, FluidConduitData> network) {
        return null;
    }

    @Override
    public FluidConduitData createConduitData(ConduitType<FluidConduitOptions, ConduitNetworkContext.Dummy, FluidConduitData> type, Level level, BlockPos pos) {
        return new FluidConduitData(type.options().isMultiFluid());
    }

    @Override
    public boolean canBeInSameBundle(FluidConduitOptions options, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.graphType() != this) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeReplacedBy(FluidConduitOptions options, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.graphType() != this) {
            return false;
        }

        if (conduitType.options() instanceof FluidConduitOptions otherOptions) {
            // Replacement must support multi fluid if the current does.
            if (options.isMultiFluid() && !otherOptions.isMultiFluid()) {
                return false;
            }

            return options.transferRate() <= otherOptions.transferRate();
        }

        return false;
    }

    @Override
    public boolean canApplyUpgrade(FluidConduitOptions fluidConduitOptions, SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return conduitUpgrade instanceof ExtractionSpeedUpgrade;
    }

    @Override
    public boolean canApplyFilter(FluidConduitOptions fluidConduitOptions, SlotType slotType, ResourceFilter resourceFilter) {
        return resourceFilter instanceof FluidStackFilter;
    }
}
