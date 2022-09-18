package com.enderio.api.conduit;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ConduitItemFactory {

    /**
     * this field is set by EIOConduits during the FMLConstructModEvent.
     */
    @Nullable
    private static BiFunction<Supplier<? extends IConduitType<?>>, Item.Properties, Item> factory = null;

    @ApiStatus.Internal
    public static void setFactory(BiFunction<Supplier<? extends IConduitType<?>>, Item.Properties, Item> factory) {
        ConduitItemFactory.factory = factory;
    }

    /**
     * This will create a Conduit Item. If, during porting, EIOConduits isn't available, a dummy item will be returned.
     * @return
     */
    public static Item build(Supplier<? extends IConduitType<?>> type, Item.Properties properties) {
        if (factory != null)
            return factory.apply(type, properties);
        return new Item(properties);
    }
}
