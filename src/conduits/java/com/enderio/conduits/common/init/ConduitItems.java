package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.IConduitType;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.function.Supplier;

public class ConduitItems {
    private static final ItemRegistry ITEM_REGISTRY = ItemRegistry.createRegistry(EnderIO.MODID);

    public static final RegiliteItem<Item> ENERGY = createConduitItem(EnderConduitTypes.ENERGY, "energy");
    public static final RegiliteItem<Item> FLUID = createConduitItem(EnderConduitTypes.FLUID, "fluid");
    public static final RegiliteItem<Item> PRESSURIZED_FLUID = createConduitItem(EnderConduitTypes.FLUID2, "pressurized_fluid");
    public static final RegiliteItem<Item> ENDER_FLUID = createConduitItem(EnderConduitTypes.FLUID3, "ender_fluid");
    public static final RegiliteItem<Item> REDSTONE = createConduitItem(EnderConduitTypes.REDSTONE, "redstone");
    public static final RegiliteItem<Item> ITEM = createConduitItem(EnderConduitTypes.ITEM, "item");

    private static RegiliteItem<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName) {
        return ITEM_REGISTRY
            .registerItem(itemName + "_conduit",
                p -> ConduitApi.INSTANCE.createConduitItem(type, p))
            .setTab(EIOCreativeTabs.CONDUITS)
            .setModelProvider((prov, ctx) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()));
    }

    public static void register(IEventBus bus) {
        ITEM_REGISTRY.register(bus);
    }
}
