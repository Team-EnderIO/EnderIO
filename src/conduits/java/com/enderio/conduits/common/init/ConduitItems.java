package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.IConduitType;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.conduits.common.blockentity.RegisteredConduitType;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ConduitItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final Map<RegistryObject<IConduitType>, ItemEntry<Item>> CONDUITS = Util.make(() -> {
        Map<RegistryObject<IConduitType>, ItemEntry<Item>> map = new HashMap<>();
        map.put(EnderConduitTypes.POWER, createConduitItem(EnderConduitTypes.POWER, "power1"));
        map.put(EnderConduitTypes.POWER2, createConduitItem(EnderConduitTypes.POWER2, "power2"));
        map.put(EnderConduitTypes.POWER3, createConduitItem(EnderConduitTypes.POWER3, "power3"));
        map.put(EnderConduitTypes.REDSTONE, createConduitItem(EnderConduitTypes.REDSTONE, "redstone"));
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

    private static ItemEntry<Item> createConduitItem(Supplier<IConduitType> type, String itemName) {
        return REGISTRATE.item(itemName + "_conduit",
            properties -> ConduitItemFactory.build(type, properties))
            .tab(() -> EIOCreativeTabs.CONDUITS)
            .register();
    }

    public static void register() {}
}
