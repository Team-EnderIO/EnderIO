package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.IConduitType;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.items.FilterItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ConduitItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final ItemEntry<Item> ENERGY = createConduitItem(EnderConduitTypes.ENERGY, "energy");
    public static final ItemEntry<Item> FLUID = createConduitItem(EnderConduitTypes.FLUID, "fluid");
    public static final ItemEntry<Item> PRESSURIZED_FLUID = createConduitItem(EnderConduitTypes.FLUID2, "pressurized_fluid");
    public static final ItemEntry<Item> ENDER_FLUID = createConduitItem(EnderConduitTypes.FLUID3, "ender_fluid");
    public static final ItemEntry<Item> REDSTONE = createConduitItem(EnderConduitTypes.REDSTONE, "redstone");
    public static final ItemEntry<Item> ITEM = createConduitItem(EnderConduitTypes.ITEM, "item");

    public static final ItemEntry<FilterItem> BASIC_ITEM_FILTER = filterItemBasic("basic_item_filter").register();
    public static final ItemEntry<FilterItem> ADVANCED_ITEM_FILTER = filterItemBasic("advanced_item_filter").register(); // "advanced" mechanics not implemented
    public static final ItemEntry<FilterItem> BIG_ITEM_FILTER = filterItemBig("big_item_filter").register();
    public static final ItemEntry<FilterItem> BIG_ADVANCED_ITEM_FILTER = filterItemBig("big_advanced_item_filter").register(); // "advanced" mechanics not implemented

    private static ItemEntry<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName) {
        return REGISTRATE.item(itemName + "_conduit",
            properties -> ConduitItemFactory.build(type, properties))
            .tab(EIOCreativeTabs.CONDUITS)
            .model((ctx, prov) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()))
            .register();
    }

    private static ItemBuilder<FilterItem, Registrate> filterItemBasic(String name) {
        return REGISTRATE.item(name, props -> FilterItem.basic()).tab(EIOCreativeTabs.MAIN);
    }

    private static ItemBuilder<FilterItem, Registrate> filterItemBig(String name) {
        return REGISTRATE.item(name, props -> FilterItem.large()).tab(EIOCreativeTabs.MAIN);
    }

    public static void register() {}
}
