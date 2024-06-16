package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FluidConduitType extends TieredConduit<FluidConduitData> {

    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    private final boolean isMultiFluid;
    private final int transferRate;
    private final ConduitTicker<FluidConduitData> ticker;

    public FluidConduitType(int tier, boolean isMultiFluid) {
        // TODO 1.21: forge:fluid is completely wrong, no?
        super(ResourceLocation.parse("forge:fluid"), (isMultiFluid ? 100_000 : 0) + tier);
        this.isMultiFluid = isMultiFluid;
        this.transferRate = tier;

        ticker = new FluidConduitTicker(!isMultiFluid, transferRate);
    }

    @Override
    public ConduitTicker<FluidConduitData> getTicker() {
        return ticker;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
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
}
