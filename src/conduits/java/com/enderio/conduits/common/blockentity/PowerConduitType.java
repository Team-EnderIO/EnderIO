package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.conduit.ticker.PowerConduitTicker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PowerConduitType extends TieredConduit {

    public PowerConduitType(ResourceLocation texture, int tier, @Nullable Supplier<Item> conduitItem) {
        super(texture, new ResourceLocation("forge", "energy"), tier, conduitItem);
    }

    @Override
    public IConduitTicker getTicker() {
        return new PowerConduitTicker(getTier());
    }
}
