package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.regilite.data.RegiliteDataProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnderIO.MODID);

    public static final ResourceKey<CreativeModeTab> MAIN = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "main"));
    public static final ResourceKey<CreativeModeTab> GEAR = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "gear"));
    public static final ResourceKey<CreativeModeTab> BLOCKS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "blocks"));
    public static final ResourceKey<CreativeModeTab> MACHINES = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "machines"));
    public static final ResourceKey<CreativeModeTab> SOULS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "souls"));
    public static final ResourceKey<CreativeModeTab> CONDUITS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "conduits"));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = createTab(MAIN, "main", "Ender IO",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_NONE.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GEAR_TAB = createTab(GEAR, "gear", "Ender IO Gear",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_ITEMS.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKS_TAB = createTab(BLOCKS, "blocks", "Ender IO Blocks",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MATERIALS.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN, GEAR));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MACHINES_TAB = createTab(MACHINES, "machines", "Ender IO Machines",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MACHINES.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN, GEAR, BLOCKS));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SOULS_TAB = createTab(SOULS, "souls", "Ender IO Souls",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MOBS.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN, GEAR, BLOCKS, MACHINES));

        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CONDUITS_TAB = createTab(CONDUITS, "conduits", "Ender IO Conduits",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_CONDUITS.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN, GEAR, BLOCKS, MACHINES, SOULS));

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> createTab(ResourceKey<CreativeModeTab> key, String name, String translation, Consumer<CreativeModeTab.Builder> builder) {
        return CREATIVE_MODE_TABS.register(name, () -> {
            CreativeModeTab.Builder config = CreativeModeTab.builder()
                .title(RegiliteDataProvider.addTranslation("itemGroup", key.location(), translation));
            builder.accept(config);
            return config.build();
        });
    }

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
