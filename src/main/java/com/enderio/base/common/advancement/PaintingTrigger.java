package com.enderio.base.common.advancement;

import com.enderio.EnderIO;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class PaintingTrigger extends SimpleCriterionTrigger<PaintingTrigger.TriggerInstance> {
    static final ResourceLocation ID = EnderIO.loc("create_painted_block");
    public static final PaintingTrigger PAINTING_TRIGGER = CriteriaTriggers.register(EnderIO.loc("painting").toString(), new PaintingTrigger());
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject jsonObject, Optional<ContextAwarePredicate> optional, DeserializationContext deserializationContext) {
        var rl = new ResourceLocation(jsonObject.get("paint").getAsString());
        return new TriggerInstance(BuiltInRegistries.BLOCK.get(rl), optional);
    }

    public void trigger(ServerPlayer pPlayer, Block paint) {
        super.trigger(pPlayer, triggerInstance -> triggerInstance.matches(paint));
    }

    public void register() {
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final Block paint;
        public TriggerInstance(Block paint, Optional<ContextAwarePredicate> optional) {
            super(optional);
            this.paint = paint;
        }

        public boolean matches(Block paint) {
            return this.paint == paint;
        }

        @Override
        public JsonObject serializeToJson() {
            JsonObject jsonobject = super.serializeToJson();
            jsonobject.addProperty("paint", BuiltInRegistries.BLOCK.getKey(paint).toString());
            return jsonobject;
        }
    }
}
