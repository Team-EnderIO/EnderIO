package com.enderio.enderio.content.travel.travel_anchor;

import com.enderio.enderio.content.paint.block.PaintedBlock;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class PaintedTravelAnchorBlock extends TravelAnchorBlock<PaintedTravelAnchorBlockEntity>
        implements PaintedBlock {

    public PaintedTravelAnchorBlock(Properties props) {
        super(EIOBlockEntities.PAINTED_TRAVEL_ANCHOR::get, props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PaintedTravelAnchorBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return getPaintedStack(level, pos, this);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndLightGetter level, BlockPos pos, Direction side,
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
