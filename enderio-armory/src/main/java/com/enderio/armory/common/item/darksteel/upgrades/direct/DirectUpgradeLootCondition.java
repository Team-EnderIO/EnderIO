package com.enderio.armory.common.item.darksteel.upgrades.direct;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.base.api.integration.IntegrationManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class DirectUpgradeLootCondition implements LootItemCondition {

    private static final MapCodec<DirectUpgradeLootCondition> CODEC = MapCodec.unit(new DirectUpgradeLootCondition());

    public static final LootItemConditionType HAS_DIRECT_UPGRADE = new LootItemConditionType(CODEC);

    @Override
    public LootItemConditionType getType() {
        return HAS_DIRECT_UPGRADE;
    }

    @Override
    public boolean test(LootContext context) {
        if (context.hasParam(LootContextParams.DAMAGE_SOURCE) && context.hasParam(LootContextParams.ATTACKING_ENTITY)
                && context.getParam(LootContextParams.ATTACKING_ENTITY) instanceof Player) {
            DamageSource damageSource = context.getParam(LootContextParams.DAMAGE_SOURCE);
            ItemStack weapon = damageSource.getWeaponItem();
            return weapon != null && DarkSteelHelper.hasUpgrade(weapon, DirectUpgrade.NAME);
        } else if (!context.hasParam(LootContextParams.TOOL) || !context.hasParam(LootContextParams.THIS_ENTITY)) {
            return false;
        }
        return (DarkSteelHelper.hasUpgrade(context.getParam(LootContextParams.TOOL), DirectUpgrade.NAME)
                || IntegrationManager.anyMatch(
                        integration -> integration.canMineWithDirect(context.getParam(LootContextParams.TOOL))))
                && context.getParam(LootContextParams.THIS_ENTITY) instanceof Player;
    }

}
