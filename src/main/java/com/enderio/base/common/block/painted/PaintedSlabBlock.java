package com.enderio.base.common.block.painted;

import com.enderio.base.EIONBTKeys;
import com.enderio.base.common.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
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
                tag.putString(EIONBTKeys.PAINT, ForgeRegistries.BLOCKS.getKey(paintedBlockEntity.getPaint2()).toString());
            } else {
                tag.putString(EIONBTKeys.PAINT, ForgeRegistries.BLOCKS.getKey(paintedBlockEntity.getPaint()).toString());
            }
            stack.getOrCreateTag().put(BlockItem.BLOCK_ENTITY_TAG, tag);
        }
        return stack;
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState,
        @Nullable BlockPos queryPos) {
        if (level.getBlockEntity(pos) instanceof DoublePaintedBlockEntity painted) {
            var paint1 = painted.getPaint();
            var paint2 = painted.getPaint2();
            if (side == Direction.UP && paint2 != null)
                return paint2.defaultBlockState();
            if (side == Direction.DOWN && paint1 != null)
                return paint1.defaultBlockState();
            if (paint1 != null)
                return paint1.defaultBlockState();
            if (paint2 != null)
                return paint2.defaultBlockState();
        }
        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }
}
