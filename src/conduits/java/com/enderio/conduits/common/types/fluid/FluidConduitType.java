package com.enderio.conduits.common.types.fluid;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.Vector2i;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FluidConduitType extends TieredConduit<FluidExtendedData> {

    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
    private final boolean isMultiFluid;
    private final int transferRate;

    public FluidConduitType(ResourceLocation texture, int tier, boolean isMultiFluid, ResourceLocation iconTexture, Vector2i iconTexturePos) {
        super(texture, new ResourceLocation("forge:fluid"), (isMultiFluid ? 100_000 : 0) + tier, iconTexture, iconTexturePos);
        this.isMultiFluid = isMultiFluid;
        this.transferRate = tier;
        this.clientConduitData = new FluidClientData(iconTexture, iconTexturePos);
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
    public FluidExtendedData createExtendedConduitData(Level level, BlockPos pos) {
        return new FluidExtendedData(isMultiFluid);
    }
}
