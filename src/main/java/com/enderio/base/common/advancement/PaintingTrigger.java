package com.enderio.base.common.advancement;

import com.enderio.EnderIO;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class PaintingTrigger extends SimpleCriterionTrigger<PaintingTrigger.TriggerInstance> {

    static final ResourceLocation ID = EnderIO.loc("create_painted_block");
    public static final PaintingTrigger PAINTING_TRIGGER = CriteriaTriggers.register(EnderIO.loc("painting").toString(), new PaintingTrigger());
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer pPlayer, Block paint) {
        super.trigger(pPlayer, triggerInstance -> triggerInstance.matches(paint));
    }

    public void register() {
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Block paint)
        implements SimpleInstance {

        private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("paint").forGetter(TriggerInstance::paint)
            ).apply(instance, TriggerInstance::new));

        public boolean matches(Block paint) {
            return this.paint == paint;
        }

        public static Criterion<TriggerInstance> painted(Block paint) {
            return PAINTING_TRIGGER.createCriterion(new TriggerInstance(Optional.empty(), paint));
        }
    }
}
