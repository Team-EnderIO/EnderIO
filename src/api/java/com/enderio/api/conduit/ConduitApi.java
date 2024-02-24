package com.enderio.api.conduit;

import net.minecraft.world.item.Item;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface ConduitApi {

    ConduitApi INSTANCE = ServiceLoader.load(ConduitApi.class).findFirst().orElseThrow();
    /**
     * This will create a Conduit Item. If, during porting, EIOConduits isn't available, a dummy item will be returned.
     */
    Item createConduitItem(Supplier<? extends IConduitType<?>> type, Item.Properties properties);
}
