package com.enderio.base.common.block.plants;

import com.enderio.base.common.init.EIOBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.TriState;

public class ExpPlant extends DoublePlantBlock implements SculkBehaviour {

    private static final VoxelShape FULL_UPPER_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    public static final int MAX_CHARGE = 300;
    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, MAX_CHARGE);
    private static final int DOUBLE_PLANT_CHARGE_INTERSECTION = 50;

    public ExpPlant(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CHARGE, 0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return FULL_UPPER_SHAPE;
    }

    private static boolean isDouble(int charge) {
        return charge >= DOUBLE_PLANT_CHARGE_INTERSECTION;
    }

    private boolean isMaxAge(BlockState state) {
        return state.getValue(CHARGE) >= MAX_CHARGE;
    }

    private static boolean isLower(BlockState state) {
        return state.is(EIOBlocks.EXP_PLANT) && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.SCULK) || (state.is(Tags.Blocks.VILLAGER_FARMLANDS) && pos.getY() < -1);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        TriState soilDecision = level.getBlockState(pos.below()).canSustainPlant(level, pos.below(), Direction.UP, state);
        //return isLower(state) ? soilDecision.isTrue() : super.canSurvive(state, level, pos);
        return true;
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        this.spawnDestroyParticles(level, player, pos, state);
        if (state.is(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinAi.angerNearbyPiglins(player, false);
        }

        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
        return state;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return false;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Ravager && level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            level.destroyBlock(pos, true, entity);
        }

        super.entityInside(state, level, pos, entity);
    }

    private int grow(LevelAccessor level, BlockState state, BlockPos pos, int ageIncrement) {
        int newState = state.getValue(CHARGE) + ageIncrement;
        int i = Math.min(newState, MAX_CHARGE);
        if (this.canGrow(level, pos, state, i)) {
            BlockState blockstate = state.setValue(CHARGE, i);
            level.setBlock(pos, blockstate, 2);
            if (isDouble(i)) {
                level.setBlock(pos.above(), blockstate.setValue(HALF, DoubleBlockHalf.UPPER), 3);
            }
        }
        return newState >= MAX_CHARGE ? MAX_CHARGE - newState : ageIncrement;
    }

    private boolean canGrow(LevelReader reader, BlockPos pos, BlockState state, int age) {
        return !this.isMaxAge(state) && (!isDouble(age) || canGrowInto(reader, pos.above()));
    }

    // TODO hook this up
    private static boolean lowLight(LevelReader level, BlockPos pos) {
        return level.getRawBrightness(pos, 0) <= 4;
    }

    private static boolean canGrowInto(LevelReader level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        return blockstate.isAir() || blockstate.is(EIOBlocks.EXP_PLANT);
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor chargeCursor, LevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource,
        SculkSpreader sculkSpreader, boolean shouldConvertBlocks) {
        int i = chargeCursor.getCharge();
        return grow(levelAccessor, levelAccessor.getBlockState(chargeCursor.getPos()), chargeCursor.getPos(), i);
    }
}
