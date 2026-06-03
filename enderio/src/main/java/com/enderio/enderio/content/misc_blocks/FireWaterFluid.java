package com.enderio.enderio.content.misc_blocks;

import com.enderio.enderio.content.fire_crafting.FireCraftingManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class FireWaterFluid extends BaseFlowingFluid {

    protected FireWaterFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    @Override
    public void randomTick(ServerLevel level, BlockPos pos, FluidState state, RandomSource random) {
        performFireCrafting(level, pos);
        spreadFire(level, pos, random);
    }

    private void performFireCrafting(ServerLevel level, BlockPos pos) {
        var fireCraftingManager = level.getData(FireCraftingManager.ATTACHMENT_TYPE);
        fireCraftingManager.tryPerformFireCrafting(level, pos);
    }

    // Fire logic copied from LavaFluid.
    private void spreadFire(ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.canSpreadFireAround(pos)) {
            return;
        }

        int i = random.nextInt(3);
        if (i > 0) {
            BlockPos blockpos = pos;

            for(int j = 0; j < i; ++j) {
                blockpos = blockpos.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                if (!level.isLoaded(blockpos)) {
                    return;
                }

                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.isAir()) {
                    if (this.hasFlammableNeighbours(level, blockpos)) {
                        level.setBlockAndUpdate(blockpos, EventHooks.fireFluidPlaceBlockEvent(level, blockpos, pos, BaseFireBlock.getState(level, blockpos)));
                        return;
                    }
                } else if (blockstate.blocksMotion()) {
                    return;
                }
            }
        } else {
            for(int k = 0; k < 3; ++k) {
                BlockPos blockpos1 = pos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                if (!level.isLoaded(blockpos1)) {
                    return;
                }

                if (level.isEmptyBlock(blockpos1.above()) && this.isFlammable(level, blockpos1, Direction.UP)) {
                    level.setBlockAndUpdate(blockpos1.above(), EventHooks.fireFluidPlaceBlockEvent(level, blockpos1.above(), pos, BaseFireBlock.getState(level, blockpos1)));
                }
            }
        }
    }

    private boolean hasFlammableNeighbours(LevelReader level, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.isFlammable(level, pos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }

        return false;
    }

    private boolean isFlammable(LevelReader level, BlockPos pos, Direction face) {
        if (level.isInsideBuildHeight(pos.getY()) && !level.hasChunkAt(pos)) {
            return false;
        } else {
            BlockState state = level.getBlockState(pos);
            return state.ignitedByLava(level, pos, face);
        }
    }

    public static class Flowing extends FireWaterFluid {
        public Flowing(Properties properties) {
            super(properties);
            this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends FireWaterFluid {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return 8;
        }
    }
}
