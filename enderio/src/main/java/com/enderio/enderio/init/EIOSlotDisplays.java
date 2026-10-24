package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.recipe.PaintSlotDisplay;
import com.enderio.enderio.foundation.recipe.SizedSlotDisplay;
import com.enderio.enderio.foundation.recipe.SoulIngredientSlotDisplay;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.SlotDisplay.Type;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOSlotDisplays {
    private static final DeferredRegister<Type<?>> SLOT_DISPLAYS = DeferredRegister.create(Registries.SLOT_DISPLAY, EnderIO.MOD_ID);

    public static final DeferredHolder<Type<?>, Type<SizedSlotDisplay>> SIZED = SLOT_DISPLAYS.register("sized", () -> SizedSlotDisplay.TYPE);
    public static final DeferredHolder<Type<?>, Type<SoulIngredientSlotDisplay>> SOUL = SLOT_DISPLAYS.register("soul", () -> SoulIngredientSlotDisplay.TYPE);
    public static final DeferredHolder<Type<?>, Type<PaintSlotDisplay>> PAINT = SLOT_DISPLAYS.register("paint", () -> PaintSlotDisplay.TYPE);

    public static void register(IEventBus bus) {
        SLOT_DISPLAYS.register(bus);
    }
}
