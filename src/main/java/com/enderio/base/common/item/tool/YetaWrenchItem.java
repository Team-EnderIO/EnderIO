package com.enderio.base.common.item.tool;

import com.enderio.api.capability.ISideConfig;
import com.enderio.base.common.init.EIOCapabilities;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

public class YetaWrenchItem extends Item {

    public YetaWrenchItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide)
            return InteractionResult.SUCCESS;

        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        // Check for side config capability
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            LazyOptional<ISideConfig> optSideConfig = be.getCapability(EIOCapabilities.SIDE_CONFIG, pContext.getClickedFace());
            if (optSideConfig.isPresent()) {
                // Cycle state.
                optSideConfig.ifPresent(ISideConfig::cycleMode);
                return InteractionResult.SUCCESS;
            }
        }

        // Look for rotation property
        BlockState state = level.getBlockState(pContext.getClickedPos());
        Optional<Either<DirectionProperty, EnumProperty<Direction.Axis>>> property = getRotationProperty(state);
        if (property.isPresent()) {
            if (pContext.getLevel().isClientSide)
                return InteractionResult.SUCCESS;
            state = getNextState(pContext, state, property.get());
            pContext.getLevel().setBlock(
                pContext.getClickedPos(),
                state,
                Block.UPDATE_NEIGHBORS + Block.UPDATE_CLIENTS);
            return InteractionResult.CONSUME;
        }
        return super.useOn(pContext);
    }

    @SuppressWarnings("unchecked")
    private static Optional<Either<DirectionProperty, EnumProperty<Direction.Axis>>> getRotationProperty(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof DirectionProperty directionProperty
                && directionProperty.getName().equals("facing")) {

                return Optional.of(Either.left(directionProperty));
            }
            if (property instanceof EnumProperty enumProperty
                && enumProperty.getName().equals("axis")
                && enumProperty.getValueClass().equals(Direction.Axis.class)) {

                return Optional.of(Either.right(enumProperty));
            }
        }
        return Optional.empty();
    }

    private static BlockState getNextState(UseOnContext pContext, BlockState state, Either<DirectionProperty, EnumProperty<Direction.Axis>> property) {
        return handleProperties(pContext, state, property.left(), property.right());
    }

    private static BlockState handleProperties(UseOnContext pContext, BlockState state, Optional<DirectionProperty> directionProperty, Optional<EnumProperty<Direction.Axis>> axisProperty) {
         if (directionProperty.isPresent())
             return handleProperty(pContext, state, directionProperty.get());
         if (axisProperty.isPresent())
             return handleProperty(pContext, state, axisProperty.get());

         throw new IllegalArgumentException("Atleast one Optional should be set");
    }

    private static <T extends Comparable<T>> BlockState handleProperty(UseOnContext pContext, BlockState state, Property<T> property) {
        int noValidStateIndex = 0;
        do {
            state = getNextBlockState(state, property);
            noValidStateIndex++;
        } while (noValidStateIndex != property.getPossibleValues().size()
            && !state.getBlock().canSurvive(state, pContext.getLevel(), pContext.getClickedPos()));
        return state;
    }

    private static <T extends Comparable<T>> BlockState getNextBlockState(BlockState currentState, Property<T> property) {
        return currentState.setValue(property, getNextValue(currentState.getValue(property), property));
    }
    private static <T extends Comparable<T>> T getNextValue(T value, Property<T> property) {
        boolean foundValid = false;
        for (T possibleValue : property.getPossibleValues()) {
            if (foundValid)
                return possibleValue;
            foundValid = possibleValue == value;
        }
        return property.getPossibleValues().iterator().next();
    }

}
