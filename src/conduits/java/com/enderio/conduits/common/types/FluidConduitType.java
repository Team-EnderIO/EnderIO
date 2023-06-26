package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FluidConduitType extends TieredConduit<FluidExtendedData> {

    public static final IConduitMenuData menuData = new IConduitMenuData.Simple(false, false, false, false, false, true);
    private final boolean isMultiFluid;
    private final int transferRate;

    public FluidConduitType(ResourceLocation texture, int tier, boolean isMultiFluid, ResourceLocation iconTexture, Vector2i iconTexturePos) {
        super(texture, new ResourceLocation("forge:fluid"), (isMultiFluid ? 100_000 : 0) + tier, iconTexture, iconTexturePos);
        this.isMultiFluid = isMultiFluid;
        this.transferRate = tier;
        this.clientConduitData = new FluidClientData(iconTexture, iconTexturePos);
    }

    @Override
    public IConduitTicker getTicker() {
        return new FluidConduitTicker(!isMultiFluid, transferRate);
    }

    @Override
    public IConduitMenuData getMenuData() {
        return menuData;
    }

    @Override
    public FluidExtendedData createExtendedConduitData(Level level, BlockPos pos) {
        return new FluidExtendedData(isMultiFluid);
    }
}
