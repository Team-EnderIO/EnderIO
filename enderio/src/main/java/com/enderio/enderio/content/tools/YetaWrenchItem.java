package com.enderio.enderio.content.tools;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.io.SideConfig;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

public class YetaWrenchItem extends Item {

    public YetaWrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        // Check for side config capability
        SideConfig sideConfig = level.getCapability(EnderIOCapabilities.SIDE_CONFIG, pos, context.getClickedFace());
        if (sideConfig != null) {
            sideConfig.cycleMode();
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Look for rotation property
        BlockState state = level.getBlockState(context.getClickedPos());
        Optional<Either<DirectionProperty, EnumProperty<Direction.Axis>>> property = getRotationProperty(state);
        if (property.isPresent()) {
            BlockState newState = getNextState(context, state, property.get());
            context.getLevel()
                    .setBlock(context.getClickedPos(), newState, Block.UPDATE_NEIGHBORS + Block.UPDATE_CLIENTS);
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, context);
    }

    @SuppressWarnings("unchecked")
    private static Optional<Either<DirectionProperty, EnumProperty<Direction.Axis>>> getRotationProperty(
            BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof DirectionProperty directionProperty
                    && directionProperty.getName().equals("facing")) {

                return Optional.of(Either.left(directionProperty));
            }
            if (property instanceof EnumProperty enumProperty && enumProperty.getName().equals("axis")
                    && enumProperty.getValueClass().equals(Direction.Axis.class)) {

                return Optional.of(Either.right(enumProperty));
            }
        }
        return Optional.empty();
    }

    private static BlockState getNextState(UseOnContext context, BlockState state,
            Either<DirectionProperty, EnumProperty<Direction.Axis>> property) {

        if (property.left().isPresent()) {
            return handleProperty(context, state, property.left().get());
        } else if (property.right().isPresent()) {
            return handleProperty(context, state, property.right().get());
        } else {
            throw new IllegalArgumentException("property must either be a Direction or Axis property.");
        }
    }

    private static <T extends Comparable<T>> BlockState handleProperty(UseOnContext context, BlockState state,
            Property<T> property) {
        int noValidStateIndex = 0;
        do {
            state = getNextBlockState(state, property);
            noValidStateIndex++;
        } while (noValidStateIndex != property.getPossibleValues().size()
                && !state.canSurvive(context.getLevel(), context.getClickedPos()));

        return state;
    }

    private static <T extends Comparable<T>> BlockState getNextBlockState(BlockState currentState,
            Property<T> property) {
        return currentState.setValue(property, getNextValue(currentState.getValue(property), property));
    }

    private static <T extends Comparable<T>> T getNextValue(T value, Property<T> property) {
        boolean foundValid = false;
        for (T possibleValue : property.getPossibleValues()) {
            if (foundValid) {
                return possibleValue;
            }

            foundValid = possibleValue == value;
        }

        return property.getPossibleValues().iterator().next();
    }
}
