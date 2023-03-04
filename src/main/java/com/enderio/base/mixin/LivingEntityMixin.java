package com.enderio.base.mixin;

import com.enderio.base.common.tag.EIOTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    /**
     * Mixin to allow everything with the enderio:tools/glider tag in the Chestplate Slot
     * @param pItem
     * @param cir
     */
    @Inject(method = "getEquipmentSlotForItem", at = @At(value = "HEAD"), cancellable = true)
    private static void enderio$allowTagGlidersInChestplateSlot(ItemStack pItem, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (pItem.is(EIOTags.Items.GLIDER)) {
            cir.setReturnValue(EquipmentSlot.CHEST);
        }
    }
}
