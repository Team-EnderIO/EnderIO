package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.conduits.common.blockentity.ConduitType;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.Util;
import net.minecraft.world.item.Item;

import java.util.EnumMap;
import java.util.Locale;

public class ConduitItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    private static final EnumMap<ConduitType, ItemEntry<Item>> CONDUITS = Util.make(() -> {
        EnumMap<ConduitType, ItemEntry<Item>> map = new EnumMap<>(ConduitType.class);
        for (ConduitType type : ConduitType.values()) {
            ItemEntry<Item> item = REGISTRATE.item(type.name().toLowerCase(Locale.ROOT) + "_conduit",
                properties -> ConduitItemFactory.build(type, properties))
                .tab(() -> EIOCreativeTabs.CONDUITS)
                .register();
            map.put(type, item);
        }
        return map;
    });


    public static void register() {}
}
