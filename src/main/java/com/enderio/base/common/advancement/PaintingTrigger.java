package com.enderio.base.common.advancement;

import com.enderio.EnderIO;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class PaintingTrigger extends SimpleCriterionTrigger<PaintingTrigger.TriggerInstance> {
    static final ResourceLocation ID = EnderIO.loc("create_painted_block");
    public static final PaintingTrigger PAINTING_TRIGGER = CriteriaTriggers.register(new PaintingTrigger());
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate player, DeserializationContext pContext) {
        var rl = new ResourceLocation(pJson.get("paint").getAsString());
        return new TriggerInstance(ForgeRegistries.BLOCKS.getValue(rl));
    }

    public void trigger(ServerPlayer pPlayer, Block paint) {
        super.trigger(pPlayer, triggerInstance -> triggerInstance.matches(paint));
    }

    public void register() {
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final Block paint;
        public TriggerInstance(Block paint) {
            super(PaintingTrigger.ID, ContextAwarePredicate.ANY);
            this.paint = paint;
        }

        public boolean matches(Block paint) {
            return this.paint == paint;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            jsonobject.addProperty("paint", ForgeRegistries.BLOCKS.getKey(paint).toString());
            return jsonobject;
        }
    }
}
