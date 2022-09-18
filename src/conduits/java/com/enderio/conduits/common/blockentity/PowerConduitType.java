package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.conduit.ticker.PowerConduitTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PowerConduitType extends TieredConduit<IExtendedConduitData.EmptyExtendedConduitData> {

    public PowerConduitType(ResourceLocation texture, int tier, @Nullable Supplier<Item> conduitItem) {
        super(texture, new ResourceLocation("forge", "energy"), tier, conduitItem);
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
