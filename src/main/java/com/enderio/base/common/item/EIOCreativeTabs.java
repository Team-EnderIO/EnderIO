package com.enderio.base.common.item;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.tterrag.registrate.Registrate;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOCreativeTabs {
    // TODO: Review creative tabs?
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final Supplier<CreativeModeTab> MAIN = REGISTRATE.buildCreativeModeTab("main", builder -> builder.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_NONE.get())), "Ender IO");
    public static final Supplier<CreativeModeTab> GEAR = REGISTRATE.buildCreativeModeTab("gear", List.of(), List.of(EnderIO.loc("main")), builder -> builder.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_ITEMS.get())), "Ender IO Gear");
    public static final Supplier<CreativeModeTab> BLOCKS = REGISTRATE.buildCreativeModeTab("blocks", List.of(), List.of(EnderIO.loc("gear")), builder -> builder.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MATERIALS.get())), "Ender IO Blocks");
    public static final Supplier<CreativeModeTab> MACHINES = REGISTRATE.buildCreativeModeTab("machines", List.of(), List.of(EnderIO.loc("blocks")), builder -> builder.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MACHINES.get())), "Ender IO Machines");
    public static final Supplier<CreativeModeTab> SOULS = REGISTRATE.buildCreativeModeTab("souls", List.of(), List.of(EnderIO.loc("machines")), builder -> builder.icon(() -> new ItemStack(EIOItems.CREATIVE_ICON_MOBS.get())), "Ender IO Souls");


    public static void register() {
        REGISTRATE.creativeModeTab(null);
    }
}
