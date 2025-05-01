package com.enderio.machines.mixin;

import com.enderio.machines.common.blocks.enderface.EnderfaceBlockEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "canInteractWithBlock", at = @At("RETURN"))
    private boolean allowIfValidEnderface(boolean original, BlockPos pos, double distanceBoost) {
        return original || EnderfaceBlockEntity.canPlayerInteractWithBlock((Player) (Object) this, this.level(), pos);
    }
}
