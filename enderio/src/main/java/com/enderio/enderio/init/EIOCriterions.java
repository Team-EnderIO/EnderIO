package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.paint.PaintingTrigger;
import com.enderio.enderio.content.tools.hang_glider.UseGliderTrigger;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOCriterions {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister
            .create(Registries.TRIGGER_TYPE, EnderIO.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, UseGliderTrigger> USE_GLIDER = TRIGGERS.register("use_glider",
            UseGliderTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PaintingTrigger> PAINTING_TRIGGER = TRIGGERS.register("painting",
            PaintingTrigger::new);

    public static void register(IEventBus modEventBus) {
        TRIGGERS.register(modEventBus);
    }

}
