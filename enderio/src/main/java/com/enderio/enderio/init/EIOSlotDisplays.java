package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.binding.display.SoulBoundSlotDisplay;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOSlotDisplays {
    private static final DeferredRegister<SlotDisplay.Type<?>> SLOT_DISPLAYS = DeferredRegister.create(Registries.SLOT_DISPLAY, EnderIO.MOD_ID);

    public static void register(IEventBus bus) {
        SLOT_DISPLAYS.register("soul_bound", () -> SoulBoundSlotDisplay.TYPE);

        SLOT_DISPLAYS.register(bus);
    }
}
