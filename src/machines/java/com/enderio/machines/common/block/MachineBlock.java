package com.enderio.machines.common.block;

import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.compat.FlywheelCompat;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class MachineBlock extends BaseEntityBlock {
    private final BlockEntityEntry<? extends MachineBlockEntity> blockEntityType;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape DEFAULT_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0); // Full block shape
    private final VoxelShape shape;

    public MachineBlock(Properties properties, BlockEntityEntry<? extends MachineBlockEntity> blockEntityType) {
        this(properties, blockEntityType, DEFAULT_SHAPE);
    }

    public MachineBlock(Properties properties, BlockEntityEntry<? extends MachineBlockEntity> blockEntityType, VoxelShape shape) {
        super(properties);
        this.blockEntityType = blockEntityType;
        this.shape = shape;
        BlockState any = this.getStateDefinition().any();
        this.registerDefaultState(any.hasProperty(FACING) ? any.setValue(FACING, Direction.NORTH) : any);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, blockEntityType.get(), MachineBlockEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        updateBlockEntityCache(pLevel, pPos);
        if (pLevel.getBlockEntity(pPos) instanceof MachineBlockEntity machineBlock) {
            machineBlock.neighborChanged(pState, pLevel, pPos, pFromPos);
        }
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        updateBlockEntityCache(level, pos);
        if (level.getBlockEntity(pos) instanceof MachineBlockEntity machineBlock) {
            machineBlock.neighborChanged(state, level, pos, neighbor);
        }
    }

    private void updateBlockEntityCache(LevelReader level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MachineBlockEntity machineBlockEntity) {
            machineBlockEntity.markCapabilityCacheDirty();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND){
            return InteractionResult.SUCCESS;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof MachineBlockEntity machineBlockEntity)) { // This also covers nulls
            return InteractionResult.PASS;
        }

        if (!level.isClientSide && player.getItemInHand(hand).is(EIOTags.Items.WRENCH)) {
            InteractionResult res = machineBlockEntity.onWrenched(player, hit.getDirection());
            if (res != InteractionResult.PASS) {
                return res;
            }
        }

        //pass on the use command to corresponding block entity.
        InteractionResult result = machineBlockEntity.onBlockEntityUsed(state, level, pos, player, hand,hit);
        if (result != InteractionResult.CONSUME) {
            if (!machineBlockEntity.canOpenMenu()) {
                return InteractionResult.PASS;
            }

            MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
            if (menuprovider != null && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, menuprovider, buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return blockEntityType.create(pPos, pState);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if (level.getBlockEntity(pos) instanceof MachineBlockEntity machineBlock) {
            return machineBlock.supportsRedstoneControl();
        }
        return super.canConnectRedstone(state, level, pos, direction);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        BlockEntity existingBlockEntity;

        // Using IForgeBlockGetter#getExistingBlockEntity() with Starlight causes chunk-loading deadlocks
        // Credit: https://github.com/XFactHD/FramedBlocks/blob/1.20/src/main/java/xfacthd/framedblocks/common/util/InternalApiImpl.java#L13-L20
        if (ModList.get().isLoaded("starlight")) {
            existingBlockEntity = level.getBlockEntity(pos);
        } else if (ModList.get().isLoaded("flywheel")) {
            existingBlockEntity = FlywheelCompat.getExistingBlockEntity(level, pos);
        } else {
            existingBlockEntity = level.getExistingBlockEntity(pos);
        }

        if (existingBlockEntity instanceof MachineBlockEntity machineBlock) {
            return machineBlock.getLightEmission();
        }
        return super.getLightEmission(state, level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return shape;
    }
}
