package com.enderio.machines.common.entity;

import com.enderio.machines.common.init.MachineEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FallingMachineEntity extends FallingBlockEntity {
    public FallingMachineEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    public FallingMachineEntity(Level level, double xo, double yo, double zo, BlockState state) {
        super(level, xo, yo, zo, state);
    }

    @Override
    public EntityType<?> getType() {
        return MachineEntities.FALLING_MACHINE.get();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
