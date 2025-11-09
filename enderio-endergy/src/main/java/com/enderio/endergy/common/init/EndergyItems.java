package com.enderio.endergy.common.init;

import com.enderio.core.common.registries.ItemDeferredRegister;
import com.enderio.endergy.common.EnderIOEndergy;
import com.enderio.endergy.common.item.TotemicCapacitorItem;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.content.capacitors.CapacitorItem;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

public class EndergyItems {
    public static final ItemDeferredRegister ITEMS = ItemDeferredRegister.create(EnderIOEndergy.MOD_ID);

    public static final DeferredItem<CapacitorItem> GRAINY_CAPACITOR = ITEMS.registerItem("grainy_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(1)));

    public static final DeferredItem<CapacitorItem> SILVER_CAPACITOR = ITEMS.registerItem("silver_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(1)));

    public static final DeferredItem<CapacitorItem> ENDERGETIC_SILVER_CAPACITOR = ITEMS.registerItem("endergetic_silver_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(2)));

    public static final DeferredItem<CapacitorItem> ENERGISED_CAPACITOR = ITEMS.registerItem("energised_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(3)));

    public static final DeferredItem<CapacitorItem> CRYSTALLINE_CAPACITOR = ITEMS.registerItem("crystalline_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(3.5f)));

    public static final DeferredItem<CapacitorItem> MELODIC_CAPACITOR = ITEMS.registerItem("melodic_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(4)));

    public static final DeferredItem<CapacitorItem> STELLAR_CAPACITOR = ITEMS.registerItem("stellar_capacitor",
        CapacitorItem::new, new Item.Properties().component(EIODataComponents.CAPACITOR_DATA, CapacitorData.simple(5)));

    public static final DeferredItem<CapacitorItem> TOTEMIC_CAPACITOR = ITEMS.registerItem("totemic_capacitor",
        TotemicCapacitorItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
