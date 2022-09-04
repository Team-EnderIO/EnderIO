package com.enderio.conduits.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PowerConduitTypes extends TieredConduit {

    public PowerConduitTypes(ResourceLocation texture, ResourceLocation type, int tier, @Nullable Supplier<Item> conduitItem) {
        super(texture, type, tier, conduitItem);
    }

}
