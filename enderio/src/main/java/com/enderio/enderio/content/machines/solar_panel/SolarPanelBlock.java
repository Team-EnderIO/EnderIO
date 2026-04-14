package com.enderio.enderio.content.machines.solar_panel;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.block.EIOEntityBlock;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SolarPanelBlock extends EIOEntityBlock<SolarPanelBlockEntity> implements AdvancedTooltipProvider {

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty NORTH_EAST = BooleanProperty.create("north_east");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH_EAST = BooleanProperty.create("south_east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty SOUTH_WEST = BooleanProperty.create("south_west");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty NORTH_WEST = BooleanProperty.create("north_west");

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 3, 16);
    private final ISolarPanelTier tier;

    public SolarPanelBlock(Supplier<BlockEntityType<? extends SolarPanelBlockEntity>> blockEntityType,
                           Properties properties, ISolarPanelTier tier) {
        super(blockEntityType, properties);

        registerDefaultState(getStateDefinition().any()
                .setValue(NORTH, true)
                .setValue(NORTH_WEST, true)
                .setValue(NORTH_EAST, true)
                .setValue(WEST, true)
                .setValue(EAST, true)
                .setValue(SOUTH_WEST, true)
                .setValue(SOUTH, true)
                .setValue(SOUTH_EAST, true));
        this.tier = tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new NotImplementedException("Block codecs are a later problem...");
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, NORTH_WEST, NORTH_EAST, WEST, EAST, SOUTH_WEST, SOUTH, SOUTH_EAST);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour,
        BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {

        if (directionToNeighbour.getAxis().getPlane() == Direction.Plane.HORIZONTAL) {
            if (directionToNeighbour == Direction.NORTH) {
                state = state.setValue(NORTH, neighbourState.getBlock() != this);
            }

            if (directionToNeighbour == Direction.EAST) {
                state = state.setValue(EAST, neighbourState.getBlock() != this);
            }

            if (directionToNeighbour == Direction.SOUTH) {
                state = state.setValue(SOUTH, neighbourState.getBlock() != this);
            }

            if (directionToNeighbour == Direction.WEST) {
                state = state.setValue(WEST, neighbourState.getBlock() != this);
            }

            state = state.setValue(NORTH_EAST,
                    state.getValue(NORTH) || state.getValue(EAST)
                            || level.getBlockState(pos.relative(Direction.NORTH).relative(Direction.EAST))
                                    .getBlock() != this);
            state = state.setValue(NORTH_WEST,
                    state.getValue(NORTH) || state.getValue(WEST)
                            || level.getBlockState(pos.relative(Direction.NORTH).relative(Direction.WEST))
                                    .getBlock() != this);
            state = state.setValue(SOUTH_EAST,
                    state.getValue(SOUTH) || state.getValue(EAST)
                            || level.getBlockState(pos.relative(Direction.SOUTH).relative(Direction.EAST))
                                    .getBlock() != this);
            state = state.setValue(SOUTH_WEST,
                    state.getValue(SOUTH) || state.getValue(WEST)
                            || level.getBlockState(pos.relative(Direction.SOUTH).relative(Direction.WEST))
                                    .getBlock() != this);
            return state;
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        tooltips.add(MachinesLang.PHOTOVOLTAIC_CELL);
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        tooltips.add(MachinesLang.PHOTOVOLTAIC_CELL_ADVANCED);
        tooltips.add(MachinesLang.PHOTOVOLTAIC_CELL_ADVANCED2);
        tooltips.add(MachinesLang.PHOTOVOLTAIC_CELL_ADVANCED3.copy()
                .append(TooltipUtil.withArgs(EIOCommonLang.ENERGY_AMOUNT, tier.getProductionRate())));
    }
}
