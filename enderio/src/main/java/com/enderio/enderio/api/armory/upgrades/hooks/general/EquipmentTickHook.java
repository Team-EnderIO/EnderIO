package com.enderio.enderio.api.armory.upgrades.hooks.general;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

/**
 * Called every server tick while the item is equipped in the slot it is intended for, i.e. worn armor or a tool in the main hand.
 * @implNote This is called every tick, so be sparing in the amount of work performed.
 */
public interface EquipmentTickHook {
    void onEquipmentTick(DarkSteelHookContext context, LivingEntity entity, EquipmentSlot slot);

    static void onEquipmentTick(DarkSteelUpgradeContainer container, DarkSteelHookContext context, LivingEntity entity, EquipmentSlot slot) {
        for (EquipmentTickHook hook : container.getHooks(DarkSteelUpgradeHookTypes.EQUIPMENT_TICK)) {
            hook.onEquipmentTick(context, entity, slot);
        }
    }
}
