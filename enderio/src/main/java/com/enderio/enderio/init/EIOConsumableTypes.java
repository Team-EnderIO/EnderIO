package com.enderio.enderio.init;

import com.enderio.enderio.content.fun.EnderiosConsumeEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOConsumableTypes {
    private static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECT_TYPES = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, "enderio");

    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<EnderiosConsumeEffect>> ENDERIOS_CONSUME_EFFECT_TYPE = CONSUME_EFFECT_TYPES.register("enderios_consume_effect",
        () -> new ConsumeEffect.Type<>(EnderiosConsumeEffect.CODEC, EnderiosConsumeEffect.STREAM_CODEC));

    public static void register(IEventBus bus) {
        CONSUME_EFFECT_TYPES.register(bus);
    }
}
