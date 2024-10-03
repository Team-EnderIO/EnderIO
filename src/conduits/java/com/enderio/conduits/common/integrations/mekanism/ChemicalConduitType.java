package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.conduits.common.integrations.Integrations;
import mekanism.api.MekanismAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

public class ChemicalConduitType extends TieredConduit<ChemicalConduitData> {
    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
    public static final ConduitMenuData MULTI_MENU_DATA = new ConduitMenuData.Simple(false, false, false, true, true, true);
    private final boolean multiFluid;

    private final Lazy<ConduitTicker<ChemicalConduitData>> ticker = Lazy.of(() -> new ChemicalTicker(getTier(),
        Integrations.MEKANISM_INTEGRATION.expectPresent().GAS_HANDLER,
        Integrations.MEKANISM_INTEGRATION.expectPresent().SLURRY_HANDLER,
        Integrations.MEKANISM_INTEGRATION.expectPresent().INFUSION_HANDLER,
        Integrations.MEKANISM_INTEGRATION.expectPresent().PIGMENT_HANDLER));

    public ChemicalConduitType(ResourceLocation tierName, int tier, boolean isMultiFluid) {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical"), tierName, tier);
        this.multiFluid = isMultiFluid;
    }

    @Override
    public ConduitTicker<ChemicalConduitData> getTicker() {
        return ticker.get();
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
