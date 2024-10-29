package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.capability.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FluidConduitType extends TieredConduit<FluidConduitData> {

    public static final ConduitMenuData NORMAL_MENU_DATA = new ConduitMenuData.Simple(true, true, true, false, false, true);
    public static final ConduitMenuData ADVANCED_MENU_DATA = new ConduitMenuData.Simple(true, true, true, true, true, true);

    private final boolean isMultiFluid;
    private final int transferRatePerTick;
    private final ConduitTicker<FluidConduitData> ticker;

    public FluidConduitType(ResourceLocation tierName, int transferRatePerTick, boolean isMultiFluid) {
        super(new ResourceLocation("forge:fluid"), tierName, /*(isMultiFluid ? 100_000 : 0) + */transferRatePerTick);
        this.isMultiFluid = isMultiFluid;
        this.transferRatePerTick = transferRatePerTick;

        ticker = new FluidConduitTicker(!isMultiFluid, this.transferRatePerTick);
    }

    @Override
    public ConduitTicker<FluidConduitData> getTicker() {
        return ticker;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return isMultiFluid ? ADVANCED_MENU_DATA : NORMAL_MENU_DATA;
    }

    @Override
    public FluidConduitData createConduitData(Level level, BlockPos pos) {
        return new FluidConduitData(isMultiFluid);
    }

    @Override
    public boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return conduitUpgrade instanceof ExtractionSpeedUpgrade;
    }

    @Override
    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return resourceFilter instanceof FluidStackFilter;
    }

    @Override
    public void addToTooltip(@Nullable Level level, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick);
        tooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_RATE_TOOLTIP, transferLimitFormatted));

        if (isMultiFluid) {
            tooltipAdder.accept(ConduitLang.MULTI_FLUID_TOOLTIP);
        }
    }
}
