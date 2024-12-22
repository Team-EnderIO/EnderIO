package com.enderio.machines.common.block;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.common.blockentity.base.LegacyMachineBlockEntity;
import com.enderio.machines.common.blockentity.solar.ISolarPanelTier;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlock extends LegacyMachineBlock implements AdvancedTooltipProvider {

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

    public SolarPanelBlock(RegiliteBlockEntity<? extends LegacyMachineBlockEntity> blockEntityType,
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, NORTH_WEST, NORTH_EAST, WEST, EAST, SOUTH_WEST, SOUTH, SOUTH_EAST);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
            LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pDirection.getAxis().getPlane() == Direction.Plane.HORIZONTAL) {
            if (pDirection == Direction.NORTH) {
                pState = pState.setValue(NORTH, pNeighborState.getBlock() != this);
            }
            if (pDirection == Direction.EAST) {
                pState = pState.setValue(EAST, pNeighborState.getBlock() != this);
            }
            if (pDirection == Direction.SOUTH) {
                pState = pState.setValue(SOUTH, pNeighborState.getBlock() != this);
            }
            if (pDirection == Direction.WEST) {
                pState = pState.setValue(WEST, pNeighborState.getBlock() != this);
            }
            pState = pState.setValue(NORTH_EAST,
                    pState.getValue(NORTH) || pState.getValue(EAST)
                            || pLevel.getBlockState(pCurrentPos.relative(Direction.NORTH).relative(Direction.EAST))
                                    .getBlock() != this);
            pState = pState.setValue(NORTH_WEST,
                    pState.getValue(NORTH) || pState.getValue(WEST)
                            || pLevel.getBlockState(pCurrentPos.relative(Direction.NORTH).relative(Direction.WEST))
                                    .getBlock() != this);
            pState = pState.setValue(SOUTH_EAST,
                    pState.getValue(SOUTH) || pState.getValue(EAST)
                            || pLevel.getBlockState(pCurrentPos.relative(Direction.SOUTH).relative(Direction.EAST))
                                    .getBlock() != this);
            pState = pState.setValue(SOUTH_WEST,
                    pState.getValue(SOUTH) || pState.getValue(WEST)
                            || pLevel.getBlockState(pCurrentPos.relative(Direction.SOUTH).relative(Direction.WEST))
                                    .getBlock() != this);
            return pState;
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        tooltips.add(MachineLang.PHOTOVOLTAIC_CELL);
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        tooltips.add(MachineLang.PHOTOVOLTAIC_CELL_ADVANCED);
        tooltips.add(MachineLang.PHOTOVOLTAIC_CELL_ADVANCED2);
        tooltips.add(MachineLang.PHOTOVOLTAIC_CELL_ADVANCED3.copy()
                .append(TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, tier.getProductionRate())));
    }
}
