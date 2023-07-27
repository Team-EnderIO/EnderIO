package com.enderio.base.common.item.darksteel.upgrades.direct;

import com.enderio.api.integration.IntegrationManager;
import com.enderio.base.common.capability.DarkSteelUpgradeable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class DirectUpgradeLootCondition implements LootItemCondition {

    public static final LootItemConditionType HAS_DIRECT_UPGRADE = new LootItemConditionType(new InnerSerializer());

    @Override
    public LootItemConditionType getType() {
        return HAS_DIRECT_UPGRADE;
    }

    @Override
    public boolean test(LootContext context) {
        if(!context.hasParam(LootContextParams.TOOL) || !context.hasParam(LootContextParams.THIS_ENTITY)) {
            return false;
        }
        return (DarkSteelUpgradeable.hasUpgrade(context.getParam(LootContextParams.TOOL), DirectUpgrade.NAME)
            || IntegrationManager.anyMatch(integration -> integration.canMineWithDirect(context.getParam(LootContextParams.TOOL))))
            && context.getParam(LootContextParams.THIS_ENTITY) instanceof Player;
    }

    private static class InnerSerializer implements Serializer<DirectUpgradeLootCondition> {

        @Override
        public void serialize(JsonObject pJson, DirectUpgradeLootCondition pValue, JsonSerializationContext pSerializationContext) {
        }

        @Override
        public DirectUpgradeLootCondition deserialize(JsonObject pJson, JsonDeserializationContext pSerializationContext) {
            return new DirectUpgradeLootCondition();
        }
    }
}
