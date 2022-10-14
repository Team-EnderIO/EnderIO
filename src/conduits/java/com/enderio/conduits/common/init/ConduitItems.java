package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.IConduitType;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.conduits.common.items.ConduitBlockItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ConduitItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final ItemEntry<Item> POWER = createConduitItem(() -> EnderConduitTypes.POWER.get(), "power1");
    public static final ItemEntry<Item> POWER2 = createConduitItem(() -> EnderConduitTypes.POWER2.get(), "power2");
    public static final ItemEntry<Item> POWER3 = createConduitItem(() -> EnderConduitTypes.POWER3.get(), "power3");
    public static final ItemEntry<Item> REDSTONE = createConduitItem(() -> EnderConduitTypes.REDSTONE.get(), "redstone");
    public static final ItemEntry<Item> ITEM = createConduitItem(() -> EnderConduitTypes.ITEM.get(), "item");

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ForgeRegistries.ITEMS.getValues().stream().filter(ConduitBlockItem.class::isInstance).forEach(item -> serverPlayer.addItem(item.getDefaultInstance()));
        }
    }

    private static ItemEntry<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName) {
        return REGISTRATE.item(itemName + "_conduit",
            properties -> ConduitItemFactory.build(type, properties))
            .tab(() -> EIOCreativeTabs.CONDUITS)
            .model((ctx, prov) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()))
            .register();
    }

    public static void register() {}
}
