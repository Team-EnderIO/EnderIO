package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import mekanism.api.MekanismAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ChemicalConduitType extends TieredConduit<ChemicalExtendedData> {
    public static final IConduitMenuData MENU_DATA = new IConduitMenuData.Simple(false, false, false, false, false, true);
    public static final IConduitMenuData MULTI_MENU_DATA = new IConduitMenuData.Simple(false, false, false, true, true, true);
    private final boolean multiFluid;

    public ChemicalConduitType(ResourceLocation texture, int tier, boolean isMultiFluid, Vector2i iconTexturePos) {
        super(texture, new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical"), tier, EnderConduitTypes.ICON_TEXTURE, iconTexturePos);
        this.multiFluid = isMultiFluid;
    }

    @Override
    public IConduitTicker getTicker() {
        return new ChemicalTicker(getTier(), MekanismIntegration.GAS, MekanismIntegration.SLURRY, MekanismIntegration.INFUSION, MekanismIntegration.PIGMENT);
    }

    @Override
    public IConduitMenuData getMenuData() {
        return multiFluid? MULTI_MENU_DATA : MENU_DATA;
    }

    @Override
    public ChemicalExtendedData createExtendedConduitData(Level level, BlockPos pos) {
        return new ChemicalExtendedData(multiFluid);
    }
}
