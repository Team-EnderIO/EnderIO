package com.enderio.base.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {
    @Shadow()
    public abstract Item getItem();

    @Override
    public boolean canGrindstoneRepair() {
        if (getItem() == Items.DEEPSLATE || getItem() == Items.COBBLED_DEEPSLATE || getItem() == Items.FLINT) {
            return true;
        }

        return IForgeItemStack.super.canGrindstoneRepair();
    }
}
