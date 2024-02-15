package com.enderio.armory.common.item.darksteel.upgrades.direct;

import com.enderio.api.integration.IntegrationManager;
import com.enderio.armory.common.capability.DarkSteelUpgradeable;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class DirectUpgradeLootCondition implements LootItemCondition {

    private static final Codec<DirectUpgradeLootCondition> CODEC = Codec.unit(new DirectUpgradeLootCondition());

    public static final LootItemConditionType HAS_DIRECT_UPGRADE = new LootItemConditionType(CODEC);

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

}
