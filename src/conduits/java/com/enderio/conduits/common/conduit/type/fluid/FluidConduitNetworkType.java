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
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidConduitNetworkType implements ConduitNetworkType<FluidConduitOptions, ConduitNetworkContext.Dummy, FluidConduitData> {

    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, true, false, false, true);
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
        if (conduitType.networkType() != this) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeReplacedBy(FluidConduitOptions options, ConduitType<?, ?, ?> conduitType) {
        if (conduitType.networkType() != this) {
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

    @Override
    public List<Component> getHoverText(FluidConduitOptions options, Item.TooltipContext context, TooltipFlag tooltipFlag) {
        // Get transfer rate, adjusted for the ticker rate.
        String transferLimitFormatted = String.format("%,d", options.transferRate() * (20 / getTicker().getTickRate()));
        Component rateTooltip = TooltipUtil.styledWithArgs(ConduitLang.FLUID_RATE_TOOLTIP, transferLimitFormatted);

        if (options.isMultiFluid()) {
            return List.of(rateTooltip, ConduitLang.MULTI_FLUID_TOOLTIP);
        }

        return List.of(rateTooltip);
    }

    @Override
    public int compare(FluidConduitOptions o1, FluidConduitOptions o2) {
        if (o1.isMultiFluid() && !o2.isMultiFluid()) {
            return 1;
        }

        if (o1.transferRate() < o2.transferRate()) {
            return -1;
        } else if (o1.transferRate() > o2.transferRate()) {
            return 1;
        }

        return 0;
    }
}
