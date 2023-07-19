package com.enderio.base.common.entity;

import com.enderio.base.EIONBTKeys;
import com.enderio.base.common.init.EIOEntities;
import com.enderio.base.common.util.PaintUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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

import java.util.Objects;

public class PaintedSandEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {

    public PaintedSandEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    public PaintedSandEntity(Level level, double x, double y, double z, BlockState state) {
        super(level, x, y, z, state);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public EntityType<?> getType() {
        return EIOEntities.PAINTED_SAND.get();
    }

    @Nullable
    public Block getPaint() {
        if (blockData != null) {
            return PaintUtils.getBlockFromRL(blockData.getString(EIONBTKeys.PAINT));
        }
        return null;
    }

    public void setPaint(Block block) {
        if (blockData == null)
            blockData = new CompoundTag();
        blockData.putString(EIONBTKeys.PAINT, Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).toString());
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        Block block = getPaint();
        buffer.writeResourceLocation(block != null ? Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)) : new ResourceLocation(""));
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
            itemNbt.put(EIONBTKeys.BLOCK_ENTITY_TAG, blockData);
            stack.setTag(itemNbt);
        }
        return super.spawnAtLocation(stack, offsetY);
    }
}
