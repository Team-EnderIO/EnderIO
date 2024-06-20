package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import mekanism.api.MekanismAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ChemicalConduitType extends TieredConduit<ChemicalConduitData> {
    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
    public static final ConduitMenuData MULTI_MENU_DATA = new ConduitMenuData.Simple(false, false, false, true, true, true);
    private final boolean multiFluid;

    public ChemicalConduitType(ResourceLocation tierName, int tier, boolean isMultiFluid) {
        super(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "chemical"), tierName, tier);
        this.multiFluid = isMultiFluid;
    }

    @Override
    public ConduitTicker<ChemicalConduitData> getTicker() {
        return new ChemicalTicker(getTier(), MekanismIntegration.GAS, MekanismIntegration.SLURRY, MekanismIntegration.INFUSION, MekanismIntegration.PIGMENT);
    }

    @Override
    public ConduitMenuData getMenuData() {
        return multiFluid? MULTI_MENU_DATA : MENU_DATA;
    }

    @Override
    public ChemicalConduitData createConduitData(Level level, BlockPos pos) {
        return new ChemicalConduitData(multiFluid);
    }
}
