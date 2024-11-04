package com.enderio.conduits.common.conduit.block;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.integration.IntegrationManager;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.conduit.ConduitGraphObject;
import com.enderio.conduits.common.conduit.RightClickAction;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.conduit.connection.StaticConnectionStates;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.enderio.conduits.common.items.ConduitProbeItem;
import com.enderio.conduits.common.network.ConduitSavedData;
import com.enderio.conduits.common.redstone.RedstoneInsertFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod.EventBusSubscriber
public class ConduitBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final boolean ENABLE_FACADES = false;

    public ConduitBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(BlockState pState, Fluid pFluid) {
        return false;
    }

    // region Water-logging

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos,
        BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // endregion

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit) {
            conduit.updateConnections(level, pos, fromPos, true);
        }

        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ConduitBlockEntities.CONDUIT.create(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            return conduit.getShape().getTotalShape();
        }
        return Shapes.block();
    }

    // region Block Interaction

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            if (!player.getItemInHand(hand).isEmpty()) {
                Optional<InteractionResult> interactionResult = handleProbe(player.getItemInHand(hand));
                if (interactionResult.isPresent()) {
                    return interactionResult.get();
                }
                
                interactionResult = addConduit(conduit, player, player.getItemInHand(hand), level.isClientSide());
                if (interactionResult.isPresent()) {
                    return interactionResult.get();
                }

                interactionResult = handleYeta(conduit, player, player.getItemInHand(hand), hit, level.isClientSide());
                if (interactionResult.isPresent()) {
                    return interactionResult.get();
                }

                interactionResult = handleFacade(conduit, player, player.getItemInHand(hand), hit, level.isClientSide());
                if (interactionResult.isPresent()) {
                    return interactionResult.get();
                }

                if (player.getItemInHand(hand).getItem() instanceof ConduitBlockItem) {
                    return super.use(state, level, pos, player, hand, hit);
                }
            }
            Optional<InteractionResult> interactionResult = handleScreen(conduit, player, hit, level.isClientSide());

            if (interactionResult.isPresent()) {
                return interactionResult.get();
            }
        }

        return super.use(state, level, pos, player, hand, hit);
    }

    private Optional<InteractionResult> handleProbe(ItemStack stack) {
        if (!(stack.getItem() instanceof ConduitProbeItem)) {
            return Optional.empty();
        }

        return Optional.of(InteractionResult.FAIL);
    }

    private Optional<InteractionResult> addConduit(ConduitBlockEntity conduit, Player player, ItemStack stack, boolean isClientSide) {
        if (!(stack.getItem() instanceof ConduitBlockItem conduitBlockItem)) {
            return Optional.empty();
        }

        RightClickAction action = conduit.addType(conduitBlockItem.getType(), player);
        if (!(action instanceof RightClickAction.Blocked)) {
            conduit.getLevel().setBlockAndUpdate(conduit.getBlockPos(), conduit.getBlockState());
        }

        InteractionResult result;

        if (action instanceof RightClickAction.Upgrade upgradeAction) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                player.getInventory().placeItemBackInInventory(upgradeAction.getNotInConduit().getConduitItem().getDefaultInstance());
            }
            result = InteractionResult.sidedSuccess(isClientSide);
        } else if (action instanceof RightClickAction.Insert) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            result = InteractionResult.sidedSuccess(isClientSide);
        } else {
            result = InteractionResult.FAIL;
        }

        if (result != InteractionResult.FAIL) {
            Level level = conduit.getLevel();
            BlockPos blockpos = conduit.getBlockPos();

            BlockState blockState = level.getBlockState(blockpos);
            SoundType soundtype = blockState.getSoundType(level, blockpos, player);
            level.playSound(player, blockpos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F,
                soundtype.getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockState));
        }

        return Optional.of(result);
    }

    private Optional<InteractionResult> handleYeta(ConduitBlockEntity conduit, Player player, ItemStack stack, BlockHitResult hit, boolean isClientSide) {
        if (stack.is(EIOTags.Items.WRENCH)) {
            @Nullable ConduitType<?> type = conduit.getShape().getConduit(hit.getBlockPos(), hit);
            @Nullable Direction direction = conduit.getShape().getDirection(hit.getBlockPos(), hit);
            if (type == null) {
                return Optional.empty();
            }

            if (isClientSide) {
                return Optional.of(InteractionResult.sidedSuccess(isClientSide));
            }

            if (direction != null) {
                ConnectionState connectionState = conduit.getBundle().getConnectionState(direction, type);
                if (connectionState instanceof DynamicConnectionState dyn) {
                    conduit.getBundle().getNodeFor(type).clearState(direction);
                    conduit.dropConnection(dyn);
                    conduit.getBundle().setConnectionState(direction, type, StaticConnectionStates.DISABLED);
                    conduit.updateShape();
                    conduit.updateConnectionToData(type);
                } else {
                    conduit.getBundle().setConnectionState(direction, type, StaticConnectionStates.DISABLED);
                    conduit.updateShape();
                    conduit.updateConnectionToData(type);

                    if (conduit.getLevel().getBlockEntity(conduit.getBlockPos().relative(direction)) instanceof ConduitBlockEntity other) {
                        other.getBundle().setConnectionState(direction.getOpposite(), type, StaticConnectionStates.DISABLED);
                        other.updateShape();
                        other.updateConnectionToData(type);
                        ConduitGraphObject<?> thisNode = conduit.getBundle().getNodeFor(type);
                        ConduitGraphObject<?> otherNode = other.getBundle().getNodeFor(type);
                        thisNode.getGraph().removeSingleEdge(thisNode, otherNode);
                        thisNode.getGraph().removeSingleEdge(otherNode, thisNode);
                        ConduitSavedData.addPotentialGraph(type, thisNode.getGraph(), (ServerLevel) conduit.getLevel());
                        ConduitSavedData.addPotentialGraph(type, otherNode.getGraph(), (ServerLevel) other.getLevel());
                    }
                }
            } else {
                ConnectionState connectionState = conduit.getBundle().getConnectionState(hit.getDirection(), type);
                if (connectionState == StaticConnectionStates.DISABLED) {
                    conduit.tryConnectTo(hit.getDirection(), type, true, true);
                }
            }

            return Optional.of(InteractionResult.sidedSuccess(isClientSide));
        }

        return Optional.empty();
    }

    @SubscribeEvent
    public static void handleShiftYeta(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(EIOTags.Items.WRENCH)
            && event.getLevel().getBlockEntity(event.getPos()) instanceof ConduitBlockEntity conduit
            && event.getEntity().isSteppingCarefully()) {

            @Nullable ConduitType<?> type = conduit.getShape().getConduit(event.getPos(), event.getHitVec());
            if (type != null) {
                conduit.removeTypeAndDelete(type);
                if (event.getLevel() instanceof ServerLevel serverLevel) {
                    Inventory inventory = event.getEntity().getInventory();
                    inventory.placeItemBackInInventory(new ItemStack(type.getConduitItem()));

                    ServerPlayer player = (ServerPlayer) event.getEntity();
                    event.getLevel().playSound(null, event.getPos(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
                        ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }

                event.setCanceled(true);
            }
        }
    }

    private Optional<InteractionResult> handleFacade(ConduitBlockEntity conduit, Player player, ItemStack stack, BlockHitResult hit, boolean isClientSide) {
        Optional<BlockState> facade = IntegrationManager.findFirst(integration -> integration.getFacadeOf(stack));
        if (facade.isPresent() && ENABLE_FACADES) {
            if (conduit.getBundle().hasFacade(hit.getDirection())) {
                return Optional.of(InteractionResult.FAIL);
            }

            conduit.getBundle().setFacade(facade.get(), hit.getDirection());
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

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
                    buf.writeInt(EIOConduitTypes.getConduitId(openInformation.get().type()));
                });
            }

            return Optional.of(InteractionResult.sidedSuccess(isClientSide));
        }

        return Optional.empty();
    }

    private Optional<OpenInformation> getOpenInformation(ConduitBlockEntity conduit, BlockHitResult hit) {
        @Nullable ConduitType<?> type = conduit.getShape().getConduit(hit.getBlockPos(), hit);
        @Nullable Direction direction = conduit.getShape().getDirection(hit.getBlockPos(), hit);

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
            for (Direction potential : Direction.values()) {
                if (canBeValidConnection(conduit, type, potential)) {
                    return Optional.of(new OpenInformation(potential, type));
                }
            }
        }
        ConduitBundle bundle = conduit.getBundle();
        //fallback
        for (Direction potential : Direction.values()) {
            if (bundle.isConnectionEnd(potential)) {
                for (ConduitType<?> potentialType : bundle.getTypes()) {
                    if (bundle.getConnectionState(potential, potentialType) instanceof DynamicConnectionState) {
                        return Optional.of(new OpenInformation(potential, potentialType));
                    }
                }
                throw new IllegalStateException("couldn't find connection even though it should be present");
            }
        }
        for (Direction potential : Direction.values()) {
            if (!(conduit.getLevel().getBlockEntity(conduit.getBlockPos().relative(potential)) instanceof ConduitBlockEntity)) {
                for (ConduitType<?> potentialType : bundle.getTypes()) {
                    if (canBeValidConnection(conduit, potentialType, potential)) {
                        return Optional.of(new OpenInformation(potential, potentialType));
                    }
                }
            }
        }

        return Optional.empty();
    }

    // endregion

    public static boolean canBeOrIsValidConnection(ConduitBlockEntity conduit, ConduitType<?> type, Direction direction) {
        ConduitBundle bundle = conduit.getBundle();

        return bundle.getConnectionState(direction, type) instanceof DynamicConnectionState
            || canBeValidConnection(conduit, type, direction);
    }

    public static boolean canBeValidConnection(ConduitBlockEntity conduit, ConduitType<?> type, Direction direction) {
        ConduitBundle bundle = conduit.getBundle();
        ConnectionState connectionState = bundle.getConnectionState(direction, type);
        return connectionState instanceof StaticConnectionStates state
            && state == StaticConnectionStates.DISABLED
            && !(conduit.getLevel().getBlockEntity(conduit.getBlockPos().relative(direction)) instanceof ConduitBlockEntity);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (level instanceof Level realLevel && state.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false)) {
            var hitResult = Item.getPlayerPOVHitResult(realLevel, player, ClipContext.Fluid.NONE);
            if (hitResult.getType() == HitResult.Type.MISS) {
                return Items.AIR.getDefaultInstance();
            }

            if (hitResult.getBlockPos().equals(pos)) {
                target = hitResult;
            } else {
                return level.getBlockState(hitResult.getBlockPos()).getCloneItemStack(hitResult, level, hitResult.getBlockPos(), player);
            }
        }

        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit) {
            @Nullable ConduitType<?> type = conduit.getShape().getConduit(pos, target);
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
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity) {
                conduitBlockEntity.everyTick();
            }
        };
    }

    // region Place and destroy logic

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        ConduitBlockItem item = (ConduitBlockItem) stack.getItem();
        if (placer instanceof Player player) {
            if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit) {
                conduit.addType(item.getType(), player);
                if (!level.isClientSide()) {
                    conduit.updateClient();
                }
            }
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        HitResult hit = player.pick(player.getBlockReach() + 5, 1, false);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            @Nullable ConduitType<?> conduitType = conduit.getShape().getConduit(((BlockHitResult) hit).getBlockPos(), hit);
            if (conduitType == null) {
                if (!conduit.getBundle().getTypes().isEmpty()) {
                    level.playSound(player, pos, SoundEvents.GENERIC_SMALL_FALL, SoundSource.BLOCKS, 1F, 1F);
                    return false;
                }
                return true;
            }

            SoundType soundtype = state.getSoundType(level, pos, player);
            level.playSound(player, pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

            if (conduit.removeType(conduitType, !player.getAbilities().instabuild)) {
                return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
            }

            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
            return false;
        }

        // No block entity, get rid of it immediately
        return true;
    }

    // endregion

    // region Redstone

    //@formatter:off
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction != null
            && level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit
            && conduit.getBundle().getTypes().contains(EIOConduitTypes.REDSTONE.get())
            && conduit.getBundle().getConnectionState(direction.getOpposite(), EIOConduitTypes.REDSTONE.get()) instanceof DynamicConnectionState;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getSignal(BlockState pBlockState, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit
            && conduit.getBundle().getTypes().contains(EIOConduitTypes.REDSTONE.get())
            && conduit.getBundle().getConnectionState(direction.getOpposite(), EIOConduitTypes.REDSTONE.get()) instanceof DynamicConnectionState dyn
            && dyn.isInsert()
            ? getSignalOutput(dyn, conduit.getBundle().getNodeFor(EIOConduitTypes.REDSTONE.get()).getConduitData()) : 0;
    }
    //@formatter:on

    private int getSignalOutput(DynamicConnectionState dyn, RedstoneConduitData data) {
        return dyn.filterInsert().getCapability(EIOCapabilities.FILTER)
            .filter(filter -> filter instanceof RedstoneInsertFilter)
            .map(filter -> ((RedstoneInsertFilter) filter).getOutputSignal(data, dyn.insertChannel()))
            .orElse(data.getSignal(dyn.insertChannel()));
    }

    // endregion

    private record OpenInformation(Direction direction, ConduitType<?> type) {}

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {

    }
}
