package com.enderio.api.conduit;

import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.conduit.ticker.PowerConduitTicker;
import com.enderio.api.misc.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PowerConduitType extends TieredConduit<IExtendedConduitData.EmptyExtendedConduitData> {

    public PowerConduitType(ResourceLocation texture, int tier, ResourceLocation iconTexture, Vector2i iconPos) {
        super(texture, new ResourceLocation("forge", "energy"), tier, iconTexture, iconPos);
    }

    @Override
    public IConduitTicker getTicker() {
        return new PowerConduitTicker(getTier());
    }

    @Override
    public IExtendedConduitData.EmptyExtendedConduitData createExtendedConduitData(Level level, BlockPos pos) {
        return new IExtendedConduitData.EmptyExtendedConduitData();
    }

    @Override
    public IConduitMenuData getMenuData() {
        return IConduitMenuData.POWER;
    }
}
