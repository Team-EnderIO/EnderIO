package com.enderio.enderio.mixin;

import com.enderio.enderio.content.armory.DoubleJumpHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    // Prevent elytra opening before double jumps are expended.
    @WrapOperation(method = "aiStep",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z"))
    private boolean doubleJumpBeforeElytra(ItemStack stack, LivingEntity entity, Operation<Boolean> original) {
        if (DoubleJumpHandler.tryClientDoubleJump((LocalPlayer) (Object) this)) {
            return false;
        }

        return original.call(stack, entity);
    }
}
