package com.enderio.core;

import com.enderio.core.common.crafting.WithCountSlotDisplay;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(EnderCore.MOD_ID)
public class EnderCore {
    // Stored here just to make sure its the same.
    // This definition is used *everywhere* else.
    public static final String MOD_ID = "endercore";

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }

    private static final DeferredRegister<SlotDisplay.Type<?>> SLOT_DISPLAYS = DeferredRegister.create(Registries.SLOT_DISPLAY, MOD_ID);

    public EnderCore(IEventBus eventBus) {
        SLOT_DISPLAYS.register("with_count", () -> WithCountSlotDisplay.TYPE);

        SLOT_DISPLAYS.register(eventBus);
    }
}
