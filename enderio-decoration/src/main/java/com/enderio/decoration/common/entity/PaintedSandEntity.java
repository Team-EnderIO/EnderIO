package com.enderio.decoration.common.entity;

import com.enderio.decoration.common.init.DecorEntities;
import com.enderio.decoration.common.util.PaintUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PaintedSandEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {

    public PaintedSandEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    public PaintedSandEntity(Level level, double x, double y, double z, BlockState state) {
        super(level, x, y, z, state);
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nonnull
    @Override
    public EntityType<?> getType() {
        return DecorEntities.PAINTED_SAND.get();
    }

    public Block getPaint() {
        if (blockData != null) {
            return PaintUtils.getBlockFromRL(blockData.getString("paint"));
        }
        return null;
    }

    public void setPaint(Block block) {
        if (blockData == null)
            blockData = new CompoundTag();
        blockData.putString("paint", Objects.requireNonNull(block.getRegistryName()).toString());
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        Block block = getPaint();
        buffer.writeResourceLocation(block != null ? Objects.requireNonNull(block.getRegistryName()) : new ResourceLocation(""));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        ResourceLocation rl = additionalData.readResourceLocation();
        Block block = ForgeRegistries.BLOCKS.getValue(rl);
        if (block != Blocks.AIR)
            setPaint(block);
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
