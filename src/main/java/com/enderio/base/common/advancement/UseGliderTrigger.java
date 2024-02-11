package com.enderio.base.common.advancement;

import com.enderio.EnderIO;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class UseGliderTrigger extends SimpleCriterionTrigger<UseGliderTrigger.TriggerInstance> {
    static final ResourceLocation ID = EnderIO.loc("use_glider");
    public static final UseGliderTrigger USE_GLIDER = CriteriaTriggers.register(EnderIO.loc("use_glider").toString(), new UseGliderTrigger());
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, Optional<ContextAwarePredicate> optional, DeserializationContext pContext) {
        return new TriggerInstance(optional);
    }

    public void trigger(ServerPlayer pPlayer) {
        super.trigger(pPlayer, triggerInstance -> true);
    }

    public void register() {
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance(Optional<ContextAwarePredicate> optional) {
            super(optional);
        }
    }
}
