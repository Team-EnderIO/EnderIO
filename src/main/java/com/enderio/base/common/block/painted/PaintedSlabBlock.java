package com.enderio.base.common.block.painted;

import com.enderio.base.common.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PaintedSlabBlock extends SlabBlock implements EntityBlock, IPaintedBlock {

    public PaintedSlabBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return EIOBlockEntities.DOUBLE_PAINTED.create(pos, state);
    }

    @Override
    public Block getPaint(BlockGetter level, BlockPos pos) {
        if (level.getBlockState(pos).getValue(SlabBlock.TYPE) != SlabType.BOTTOM
            && level.getExistingBlockEntity(pos) instanceof DoublePaintedBlockEntity paintedBlockEntity) {
            Block paint = paintedBlockEntity.getPaint2();
            if (paint != null)
                return paint;
        }
        return IPaintedBlock.super.getPaint(level, pos);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof DoublePaintedBlockEntity paintedBlockEntity) {
            CompoundTag tag = new CompoundTag();
            if (target.getLocation().y - pos.getY() > 0.5) {
                tag.putString("paint", ForgeRegistries.BLOCKS.getKey(paintedBlockEntity.getPaint2()).toString());
            } else {
                tag.putString("paint", ForgeRegistries.BLOCKS.getKey(paintedBlockEntity.getPaint()).toString());
            }
            stack.getOrCreateTag().put("BlockEntityTag", tag);
        }
        return stack;
    }
}
