package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FluidConduitType extends TieredConduit<FluidConduitData> {

    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
    private final boolean isMultiFluid;
    private final int transferRate;

    public FluidConduitType(int tier, boolean isMultiFluid) {
        super(new ResourceLocation("forge:fluid"), (isMultiFluid ? 100_000 : 0) + tier);
        this.isMultiFluid = isMultiFluid;
        this.transferRate = tier;
        this.clientConduitData = new FluidClientData();
    }

    @Override
    public ConduitTicker getTicker() {
        return new FluidConduitTicker(!isMultiFluid, transferRate);
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public FluidConduitData createConduitData(Level level, BlockPos pos) {
        return new FluidConduitData(isMultiFluid);
    }
}
