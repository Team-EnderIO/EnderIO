package com.enderio.conduits.common;

import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitType;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.items.ConduitBlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ConduitApiImpl implements ConduitApi {

    @Override
    public Item createConduitItem(Supplier<? extends ConduitType<?>> type, Item.Properties properties) {
        return new ConduitBlockItem(type, ConduitBlocks.CONDUIT.get(), properties);
    }
}
