package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.ElytraUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.GliderUpgrade;
import com.enderio.armory.common.tag.ArmoryTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

public class DarkSteelChestplateItem extends DarkSteelArmor {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_CHESTPLATE, EmpoweredUpgrade.NAME,
                        GliderUpgrade.NAME, ElytraUpgrade.NAME);
    }

    public DarkSteelChestplateItem(Properties properties) {
        super(properties, Type.CHESTPLATE);
    }

    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return DarkSteelHelper.hasUpgrade(stack, ElytraUpgrade.NAME)
                && stack.getOrDefault(ArmoryDataComponents.DARK_STEEL_FLIGHT_ACTIVE, false);
    }

    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                }
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
    }

}
