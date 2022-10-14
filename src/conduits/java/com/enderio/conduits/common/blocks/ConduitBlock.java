package com.enderio.conduits.common.blocks;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.integration.IntegrationManager;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.blockentity.ConduitBundle;
import com.enderio.conduits.common.blockentity.RightClickAction;
import com.enderio.conduits.common.blockentity.connection.DynamicConnectionState;
import com.enderio.conduits.common.blockentity.connection.IConnectionState;
import com.enderio.conduits.common.blockentity.connection.StaticConnectionStates;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.items.ConduitBlockItem;
import com.enderio.conduits.common.network.ConduitSavedData;
import com.enderio.conduits.common.types.RedstoneExtendedData;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod.EventBusSubscriber
public class ConduitBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public ConduitBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    /**
     * Don't make Conduits tick for syncing reasons. Schedule a tick without delay, if the data has changed for conduitdata
     * @param state
     * @param level
     * @param pos
     * @param random
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        Optional.ofNullable(level.getBlockEntity(pos)).ifPresent(be -> {
            if (be instanceof EnderBlockEntity enderBlockEntity) {
                enderBlockEntity.sync();
            }
        });
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos,
        BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit) {
            for (Direction direction: Direction.values()) {
                if (!(level.getBlockEntity(fromPos) instanceof ConduitBlockEntity)) {
                    ConduitBundle bundle = conduit.getBundle();
                    for (IConduitType<?> type : bundle.getTypes()) {
                        IConnectionState connectionState = bundle.getConnection(direction).getConnectionState(type, bundle);
                        if (connectionState instanceof DynamicConnectionState dyn) {
                            if (!type.getTicker().canConnectTo(level, pos, direction)) {
                                conduit.getBundle().getNodeFor(type).clearState(direction);
                                conduit.dropConnection(dyn);
                                conduit.getBundle().getConnection(direction).setConnectionState(type, conduit.getBundle(), StaticConnectionStates.DISCONNECTED);
                                conduit.updateShape();
                                conduit.updateConnectionToData(type);
                            }
                        } else if (connectionState == StaticConnectionStates.DISCONNECTED) {
                            conduit.tryConnectTo(direction, type, true);
                        }
                    }
                }
            }
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ConduitBlockEntities.CONDUIT.create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            return conduit.getShape().getTotalShape();
        }
        return Shapes.block();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            if (!player.getItemInHand(hand).isEmpty()) {
                Optional<InteractionResult> interactionResult = addConduit(conduit, player, player.getItemInHand(hand), level.isClientSide());
                if (interactionResult.isPresent())
                    return interactionResult.get();
                interactionResult = handleYeta(conduit, player, player.getItemInHand(hand), hit, level.isClientSide());
                if (interactionResult.isPresent())
                    return interactionResult.get();
                interactionResult = handleFacade(conduit, player, player.getItemInHand(hand), hit, level.isClientSide());
                if (interactionResult.isPresent())
                    return interactionResult.get();
            }
            Optional<InteractionResult> interactionResult = handleScreen(conduit, player, hit, level.isClientSide());
            if (interactionResult.isPresent())
                return interactionResult.get();
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    private Optional<InteractionResult> addConduit(ConduitBlockEntity conduit, Player player, ItemStack stack, boolean isClientSide) {
        if (!(stack.getItem() instanceof ConduitBlockItem conduitBlockItem))
            return Optional.empty();

        RightClickAction action = conduit.addType(conduitBlockItem.getType(), player);
        if (!(action instanceof RightClickAction.Blocked)) {
            conduit.getLevel().setBlockAndUpdate(conduit.getBlockPos(), conduit.getBlockState());
        }
        if (action instanceof RightClickAction.Upgrade upgradeAction) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                player.getInventory().placeItemBackInInventory(upgradeAction.getNotInConduit().getConduitItem().getDefaultInstance());
            }
            return Optional.of(InteractionResult.sidedSuccess(isClientSide));
        } else if (action instanceof RightClickAction.Insert) {
            if (!player.getAbilities().instabuild)
                stack.shrink(1);
            return Optional.of(InteractionResult.sidedSuccess(isClientSide));
        }
        return Optional.empty();
    }

    private Optional<InteractionResult> handleYeta(ConduitBlockEntity conduit, Player player, ItemStack stack, BlockHitResult hit, boolean isClientSide) {
        if (stack.is(EIOTags.Items.WRENCH)) {
            @Nullable
            IConduitType<?> type = conduit.getShape().getConduit(hit.getBlockPos(), hit);
            @Nullable
            Direction direction = conduit.getShape().getDirection(hit.getBlockPos(), hit);
            if (type == null)
                return Optional.empty();
            if (isClientSide)
                return Optional.of(InteractionResult.sidedSuccess(isClientSide));
            if (direction != null) {
                IConnectionState connectionState = conduit.getBundle().getConnection(direction).getConnectionState(type, conduit.getBundle());
                if (connectionState instanceof DynamicConnectionState dyn) {
                    conduit.getBundle().getNodeFor(type).clearState(direction);
                    conduit.dropConnection(dyn);
                    conduit.getBundle().getConnection(direction).setConnectionState(type, conduit.getBundle(), StaticConnectionStates.DISABLED);
                    conduit.updateShape();
                    conduit.updateConnectionToData(type);
                } else {
                    conduit.getBundle().getConnection(direction).setConnectionState(type, conduit.getBundle(), StaticConnectionStates.DISABLED);
                    conduit.updateShape();
                    conduit.updateConnectionToData(type);
                    if (conduit.getLevel().getBlockEntity(conduit.getBlockPos().relative(direction)) instanceof ConduitBlockEntity other) {
                        other.getBundle().getConnection(direction.getOpposite()).setConnectionState(type, other.getBundle(), StaticConnectionStates.DISABLED);
                        other.updateShape();
                        other.updateConnectionToData(type);
                        NodeIdentifier<?> thisNode = conduit.getBundle().getNodeFor(type);
                        NodeIdentifier<?> otherNode = other.getBundle().getNodeFor(type);
                        thisNode.getGraph().removeSingleEdge(thisNode, otherNode);
                        thisNode.getGraph().removeSingleEdge(otherNode, thisNode);
                        ConduitSavedData.addPotentialGraph(type, thisNode.getGraph(), (ServerLevel) conduit.getLevel());
                        ConduitSavedData.addPotentialGraph(type, otherNode.getGraph(), (ServerLevel) other.getLevel());
                    }
                }
            } else {
                IConnectionState connectionState = conduit.getBundle().getConnection(hit.getDirection()).getConnectionState(type, conduit.getBundle());
                if (connectionState == StaticConnectionStates.DISABLED) {
                    conduit.tryConnectTo(hit.getDirection(), type, true);
                }
            }
            return Optional.of(InteractionResult.sidedSuccess(isClientSide));
        }
        return Optional.empty();
    }

    @SubscribeEvent
    public static void handleShiftYeta(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(EIOTags.Items.WRENCH)) {
            if (event.getLevel().getBlockEntity(event.getPos()) instanceof ConduitBlockEntity conduit && event.getEntity().isCrouching()) {
                @Nullable
                IConduitType<?> type = conduit.getShape().getConduit(event.getPos(), event.getHitVec());
                if (type != null) {
                    conduit.removeType(type, true);
                    event.setCanceled(true);
                }
            }
        }
    }

    private Optional<InteractionResult> handleFacade(ConduitBlockEntity conduit, Player player, ItemStack stack, BlockHitResult hit, boolean isClientSide) {
        Optional<BlockState> facade = IntegrationManager.findFirst(integration -> integration.getFacadeOf(stack));
        if (facade.isPresent()) {
            if (conduit.getBundle().hasFacade(hit.getDirection())) {
                return Optional.of(InteractionResult.FAIL);
            }
            conduit.getBundle().setFacade(facade.get(), hit.getDirection());
            if (!player.getAbilities().instabuild)
                stack.shrink(1);
            return Optional.of(InteractionResult.sidedSuccess(isClientSide));
        }
        return Optional.empty();
    }

    private Optional<InteractionResult> handleScreen(ConduitBlockEntity conduit, Player player, BlockHitResult hit, boolean isClientSide) {
        Optional<OpenInformation> openInformation = getOpenInformation(conduit, hit);
        if (openInformation.isPresent()) {
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, conduit.menuProvider(openInformation.get().direction(), openInformation.get().type()), buf -> {
                    buf.writeBlockPos(conduit.getBlockPos());
                    buf.writeEnum(openInformation.get().direction());
                    buf.writeInt(ConduitTypes.getRegistry().getID(openInformation.get().type()));
                });
            }
            return Optional.of(InteractionResult.sidedSuccess(isClientSide));
        }
        return Optional.empty();
    }

    private Optional<OpenInformation> getOpenInformation(ConduitBlockEntity conduit, BlockHitResult hit) {
        @Nullable
        IConduitType<?> type = conduit.getShape().getConduit(hit.getBlockPos(), hit);
        @Nullable
        Direction direction = conduit.getShape().getDirection(hit.getBlockPos(), hit);
        //TODO figure our server check
        if (direction != null && type != null) {
            if (canBeOrIsValidConnection(conduit, type, direction)) {
                return Optional.of(new OpenInformation(direction, type));
            }
        }

        if (type != null) {
            direction = hit.getDirection();
            if (canBeValidConnection(conduit, type, direction)) {
                return Optional.of(new OpenInformation(direction, type));
            }
        }
        if (type != null) {
            for (Direction potential: Direction.values()) {
                if (canBeValidConnection(conduit, type, potential))
                    return Optional.of(new OpenInformation(potential, type));
            }
        }
        ConduitBundle bundle = conduit.getBundle();
        //fallback
        for (Direction potential: Direction.values()) {
            if (bundle.getConnection(potential).isEnd()) {
                for (IConduitType<?> potentialType: bundle.getTypes()) {
                    if (bundle.getConnection(potential).getConnectionState(potentialType, bundle) instanceof DynamicConnectionState)
                        return Optional.of(new OpenInformation(potential, potentialType));
                }
                throw new IllegalStateException("couldn't find connection even though it should be present");
            }
        }
        for (Direction potential: Direction.values()) {
            if (!(conduit.getLevel().getBlockEntity(conduit.getBlockPos().relative(potential)) instanceof ConduitBlockEntity)) {
                for (IConduitType<?> potentialType: bundle.getTypes()) {
                    if (canBeValidConnection(conduit, potentialType, potential)) {
                        return Optional.of(new OpenInformation(potential, potentialType));
                    }
                }
            }
        }

        return Optional.empty();
    }

    public static boolean canBeOrIsValidConnection(ConduitBlockEntity conduit, IConduitType<?> type, Direction direction) {
        return conduit.getBundle().getConnection(direction).getConnectionState(type,conduit.getBundle()) instanceof DynamicConnectionState
            || canBeValidConnection(conduit, type, direction);
    }
    public static boolean canBeValidConnection(ConduitBlockEntity conduit, IConduitType<?> type, Direction direction) {
        IConnectionState connectionState = conduit.getBundle().getConnection(direction).getConnectionState(type, conduit.getBundle());
        return connectionState instanceof StaticConnectionStates state
                && state == StaticConnectionStates.DISABLED
                && !(conduit.getLevel().getBlockEntity(conduit.getBlockPos().relative(direction)) instanceof ConduitBlockEntity);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit) {
            @Nullable
            IConduitType<?> type = conduit.getShape().getConduit(pos, target);
            if (type != null) {
                return type.getConduitItem().getDefaultInstance();
            }
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity)
                conduitBlockEntity.everyTick();
        };
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        HitResult hit = player.pick(player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + 5,1,false);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            @Nullable
            IConduitType<?> conduitType = conduit.getShape().getConduit(((BlockHitResult)hit).getBlockPos(), hit);
            if (conduitType == null || conduit.removeType(conduitType, !player.getAbilities().instabuild)) {
                return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
            }
        }
        return false;
    }



    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction != null
            && level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit
            && conduit.getBundle().getTypes().contains(EnderConduitTypes.REDSTONE.get())
            && conduit.getBundle().getConnection(direction.getOpposite()).getConnectionState(EnderConduitTypes.REDSTONE.get(), conduit.getBundle()) instanceof DynamicConnectionState;
    }

    public int getSignal(BlockState pBlockState, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit
            && conduit.getBundle().getTypes().contains(EnderConduitTypes.REDSTONE.get())
            && conduit.getBundle().getConnection(direction.getOpposite()).getConnectionState(EnderConduitTypes.REDSTONE.get(), conduit.getBundle()) instanceof DynamicConnectionState dyn
            && dyn.isInsert()
            && conduit.getBundle().getNodeFor(EnderConduitTypes.REDSTONE.get()) != null
            && conduit.getBundle().getNodeFor(EnderConduitTypes.REDSTONE.get()).getExtendedConduitData() instanceof RedstoneExtendedData redstoneExtendedData
            && redstoneExtendedData.isActive() ? 15 : 0;
    }

    private record OpenInformation(Direction direction, IConduitType<?> type) {
    }
}
