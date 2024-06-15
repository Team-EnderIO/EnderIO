package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.components.FluidSpeedUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FluidConduitType extends TieredConduit<FluidConduitData> {

    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    private final boolean isMultiFluid;
    private final int transferRate;
    private final ConduitTicker<FluidConduitData> ticker;

    public FluidConduitType(int tier, boolean isMultiFluid) {
        super(new ResourceLocation("forge:fluid"), (isMultiFluid ? 100_000 : 0) + tier);
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
    public boolean canApplyUpgrade(ConduitUpgrade conduitUpgrade) {
        return conduitUpgrade instanceof FluidSpeedUpgrade;
    }

    @Override
    public boolean canApplyFilter(ResourceFilter resourceFilter) {
        return resourceFilter instanceof FluidStackFilter;
    }
}
