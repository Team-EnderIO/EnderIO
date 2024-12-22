package com.enderio.machines.common.blocks.travel_anchor;

import com.enderio.base.common.paint.block.PaintedBlock;
import com.enderio.machines.common.init.MachineBlockEntities;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class PaintedTravelAnchorBlock extends TravelAnchorBlock<PaintedTravelAnchorBlockEntity>
        implements PaintedBlock {

    public PaintedTravelAnchorBlock(Properties props) {
        super(MachineBlockEntities.PAINTED_TRAVEL_ANCHOR::get, props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return MachineBlockEntities.PAINTED_TRAVEL_ANCHOR.create(pPos, pState);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
            Player player) {
        return getPaintedStack(level, pos, this);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side,
            @Nullable BlockState queryState, @Nullable BlockPos queryPos) {

        if (level.getBlockEntity(pos) instanceof PaintedTravelAnchorBlockEntity painted) {
            Optional<Block> paint = painted.getPrimaryPaint();

            if (paint.isPresent()) {
                return paint.get().defaultBlockState();
            }
        }

        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }

    @Override
    public Block getPaint(BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PaintedTravelAnchorBlockEntity paintedBlockEntity) {
            Optional<Block> paint = paintedBlockEntity.getPrimaryPaint();
            if (paint.isPresent() && !(paint.get() instanceof PaintedBlock)) {
                return paint.get();
            }
        }

        return PaintedBlock.DEFAULT_PAINT;
    }
}
