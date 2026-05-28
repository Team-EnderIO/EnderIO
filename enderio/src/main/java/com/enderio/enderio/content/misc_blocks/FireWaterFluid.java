package com.enderio.enderio.content.misc_blocks;

import com.enderio.enderio.content.fire_crafting.FireCraftingHandler;
import com.enderio.enderio.content.fire_crafting.FireCraftingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
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

    public void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        if (level instanceof ServerLevel serverLevel) {
            performFireCrafting(serverLevel, pos);
        }

        spreadFire(level, pos, random);
    }

    private void performFireCrafting(ServerLevel level, BlockPos pos) {
        // Really rubbish way of doing this, but should do fine for now.
        BlockState blockBelow = level.getBlockState(pos.below());

        FireCraftingRecipe matchingRecipe = null;
        for (var recipeHolder : FireCraftingHandler.getCachedRecipes()) {
            var recipe = recipeHolder.value();
            if (recipe.isBaseValid(blockBelow.getBlock()) && recipe.isDimensionValid(level.dimension())) {
                matchingRecipe = recipe;
                break;
            }
        }

        if (matchingRecipe == null) {
            return;
        }

        FireCraftingHandler.spawnInfinityDrops(level, pos, matchingRecipe);
    }

    // Fire logic copied from LavaFluid.
    private void spreadFire(Level level, BlockPos pos, RandomSource random) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
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

    private boolean isFlammable(LevelReader p_level, BlockPos p_pos, Direction face) {
        if (p_pos.getY() >= p_level.getMinBuildHeight() && p_pos.getY() < p_level.getMaxBuildHeight() && !p_level.hasChunkAt(p_pos)) {
            return false;
        } else {
            BlockState state = p_level.getBlockState(p_pos);
            return state.ignitedByLava(p_level, p_pos, face);
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
