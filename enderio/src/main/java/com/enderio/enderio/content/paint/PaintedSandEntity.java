package com.enderio.enderio.content.paint;

import com.enderio.enderio.foundation.EIONBTKeys;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PaintedSandEntity extends FallingBlockEntity implements IEntityWithComplexSpawn {

    public PaintedSandEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    public PaintedSandEntity(Level level, double x, double y, double z, BlockState state) {
        super(level, x, y, z, state);
    }

    @Override
    public EntityType<?> getType() {
        return EIOEntities.PAINTED_SAND.get();
    }

    @Nullable
    public Block getPaint() {
        if (blockData != null) {
            return PaintUtils.getBlockFromString(blockData.getStringOr(EIONBTKeys.PAINT, "sand")); //TODO default?
        }
        return null;
    }

    public void setPaint(Block block) {
        if (blockData == null) {
            blockData = new CompoundTag();
        }

        blockData.putString(EIONBTKeys.PAINT, Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).toString());
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        Block block = getPaint();
        // TODO: 1.21: Check this RL creation
        buffer.writeResourceLocation(block != null ? Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)) : ResourceLocation.fromNamespaceAndPath("", ""));
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        ResourceLocation rl = additionalData.readResourceLocation();
        Block block = BuiltInRegistries.BLOCK.getValue(rl);
        if (block != Blocks.AIR) {
            setPaint(block);
        }
    }

    @Override
    public @Nullable ItemEntity spawnAtLocation(ServerLevel level, ItemStack stack, float yOffset) {
        if (!stack.isEmpty() && blockData != null) {
            stack.set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(getPaint()));
        }

        return super.spawnAtLocation(level, stack, yOffset);
    }
}
