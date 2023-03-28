package com.enderio.base.common.advancement;

import com.enderio.EnderIO;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public class UseGliderTrigger extends SimpleCriterionTrigger<UseGliderTrigger.TriggerInstance> {
    static final ResourceLocation ID = EnderIO.loc("use_glider");
    public static final UseGliderTrigger USE_GLIDER = CriteriaTriggers.register(new UseGliderTrigger());
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, EntityPredicate.Composite pPlayer, DeserializationContext pContext) {
        return new TriggerInstance();
    }

    public void trigger(ServerPlayer pPlayer) {
        super.trigger(pPlayer, triggerInstance -> true);
    }

    public void register() {
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance() {
            super(UseGliderTrigger.ID, EntityPredicate.Composite.ANY);
        }
    }
}
