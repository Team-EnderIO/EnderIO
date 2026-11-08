package com.enderio.enderio.mixin;

import com.enderio.enderio.api.armory.upgrades.hooks.armor.CanFreezeHook;
import com.enderio.enderio.content.armory.DarkSteelHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyReturnValue(method = "canFreeze", at = @At("RETURN"))
    private boolean modifyCanFreezeFromDarkSteelUpgrades(boolean original) {
        if (!original) {
            return false;
        }

        LivingEntity self = (LivingEntity) (Object) this;

        // Mirrors the slots vanilla checks for FREEZE_IMMUNE_WEARABLES.
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }

            ItemStack stack = self.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            if (!CanFreezeHook.canFreeze(DarkSteelHelper.getUpgradeContainer(stack), DarkSteelHelper.getHookContext(stack), self)) {
                return false;
            }
        }

        return true;
    }
}
