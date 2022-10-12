package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.conduit.ticker.PowerConduitTicker;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class PowerConduitType extends TieredConduit<IExtendedConduitData.EmptyExtendedConduitData> {

    public PowerConduitType(ResourceLocation texture, int tier, Vector2i iconPos) {
        super(texture, new ResourceLocation("forge", "energy"), tier, EnderConduitTypes.ICON_TEXTURE, iconPos);
    }

    @Override
    public IConduitTicker getTicker() {
        return new PowerConduitTicker(getTier());
    }

    @Override
    public IExtendedConduitData.EmptyExtendedConduitData createExtendedConduitData(Level level, BlockPos pos) {
        return new IExtendedConduitData.EmptyExtendedConduitData();
    }
}
