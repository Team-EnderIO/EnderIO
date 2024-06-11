package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.paint.PaintingTrigger;
import com.enderio.base.common.advancement.UseGliderTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOCriterions {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, EnderIO.MODID);

    public static DeferredHolder<CriterionTrigger<?>, UseGliderTrigger> USE_GLIDER = TRIGGERS.register("use_glider", UseGliderTrigger::new);
    public static DeferredHolder<CriterionTrigger<?>, PaintingTrigger> PAINTING_TRIGGER = TRIGGERS.register("painting", PaintingTrigger::new);

    public static void register(IEventBus modEventBus) {
        TRIGGERS.register(modEventBus);
    }

}
