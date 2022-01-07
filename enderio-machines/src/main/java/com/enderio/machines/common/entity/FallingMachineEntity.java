package com.enderio.machines.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FallingMachineEntity extends FallingBlockEntity {
    public FallingMachineEntity(Level level, double xo, double yo, double zo, BlockState state) {
        super(level, xo, yo, zo, state);
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemStack stack, float offsetY) {
        // Add block entity NBT to item stack
        if (!stack.isEmpty() && blockData != null) {
            CompoundTag itemNbt = new CompoundTag();
            itemNbt.put("BlockEntityTag", blockData);
            stack.setTag(itemNbt);
        }
        return super.spawnAtLocation(stack, offsetY);
    }
}
