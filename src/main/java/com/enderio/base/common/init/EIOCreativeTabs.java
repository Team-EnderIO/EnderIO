package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOCreativeTabs {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final ResourceKey<CreativeModeTab> MAIN = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "main"));
    public static final ResourceKey<CreativeModeTab> GEAR = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "gear"));
    public static final ResourceKey<CreativeModeTab> BLOCKS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "blocks"));
    public static final ResourceKey<CreativeModeTab> MACHINES = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "machines"));
    public static final ResourceKey<CreativeModeTab> SOULS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "souls"));
    public static final ResourceKey<CreativeModeTab> CONDUITS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EnderIO.MODID, "conduits"));

    public static final RegistryEntry<CreativeModeTab> MAIN_TAB = createTab(MAIN, "main", "Ender IO",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_NONE.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS));

    public static final RegistryEntry<CreativeModeTab> GEAR_TAB = createTab(GEAR, "gear", "Ender IO Gear",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_ITEMS.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN));

    public static final RegistryEntry<CreativeModeTab> BLOCKS_TAB = createTab(BLOCKS, "blocks", "Ender IO Blocks",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MATERIALS.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN, GEAR));

    public static final RegistryEntry<CreativeModeTab> MACHINES_TAB = createTab(MACHINES, "machines", "Ender IO Machines",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MACHINES.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN, GEAR, BLOCKS));

    public static final RegistryEntry<CreativeModeTab> SOULS_TAB = createTab(SOULS, "souls", "Ender IO Souls",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MOBS.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN, GEAR, BLOCKS, MACHINES));

        public static final RegistryEntry<CreativeModeTab> CONDUITS_TAB = createTab(CONDUITS, "conduits", "Ender IO Conduits",
        tab -> tab.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_CONDUITS.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS, MAIN, GEAR, BLOCKS, MACHINES, SOULS));

    private static RegistryEntry<CreativeModeTab> createTab(ResourceKey<CreativeModeTab> key, String name, String english, Consumer<CreativeModeTab.Builder> config) {
        return REGISTRATE.generic(name, Registries.CREATIVE_MODE_TAB, () -> {
            var builder = CreativeModeTab.builder().title(REGISTRATE.addLang("itemGroup", key.location(), english));
            config.accept(builder);
            return builder.build();
        }).register();
    }

    public static void register() {
    }
}
