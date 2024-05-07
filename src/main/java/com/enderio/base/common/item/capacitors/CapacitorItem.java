package com.enderio.base.common.item.capacitors;

import com.enderio.base.common.blockentity.MachineInstallable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Base class for all capacitor items for common functionality
 */
public class CapacitorItem extends Item {
    public CapacitorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.getBlockEntity(pos) instanceof MachineInstallable equippable) {
            return equippable.tryItemInstall(stack, context);
        }

        return super.onItemUseFirst(stack, context);
    }
}
