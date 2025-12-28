package com.enderio.enderio.content.tools.hang_glider;

import com.enderio.enderio.EnderIO;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class UseGliderTrigger extends SimpleCriterionTrigger<UseGliderTrigger.TriggerInstance> {
    static final Identifier ID = EnderIO.rl("use_glider");
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayer pPlayer) {
        super.trigger(pPlayer, triggerInstance -> true);
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player)
        implements SimpleInstance {
        private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
        ).apply(instance, TriggerInstance::new));
    }
}
