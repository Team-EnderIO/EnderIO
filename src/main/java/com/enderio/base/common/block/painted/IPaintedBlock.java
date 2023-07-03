package com.enderio.base.common.block.painted;

import com.enderio.base.common.blockentity.SinglePaintedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;

public interface IPaintedBlock extends IForgeBlock {

    @Override
    default float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return getPaintState(level, pos).getFriction(level, pos, entity);
    }

    @Override
    default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return getPaintState(level, pos).getSoundType(level, pos, entity);
    }

    @Override
    default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        return getPaintState(level, pos).shouldDisplayFluidOverlay(level, pos, fluidState);
    }

    @Override
    default boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    default BlockState getPaintState(BlockGetter level, BlockPos pos) {
        return getPaint(level, pos).defaultBlockState();
    }

    default Block getPaint(BlockGetter level, BlockPos pos) {
        if (level.getExistingBlockEntity(pos) instanceof SinglePaintedBlockEntity paintedBlockEntity) {
            Block paint = paintedBlockEntity.getPaint();
            if (paint != null && !(paint instanceof IPaintedBlock))
                return paint;
        }
        //sane default (definitely not air)
        return Blocks.OAK_PLANKS;
    }
}
