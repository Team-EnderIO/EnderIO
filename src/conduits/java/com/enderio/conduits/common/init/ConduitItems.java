package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.conduits.common.blockentity.ConduitType;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.Locale;

@Mod.EventBusSubscriber
public class ConduitItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final EnumMap<ConduitType, ItemEntry<Item>> CONDUITS = Util.make(() -> {
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


    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            for (ItemEntry<Item> value : CONDUITS.values()) {
                serverPlayer.addItem(value.get().getDefaultInstance());
            }
        }
    }

    public static void register() {}
}
