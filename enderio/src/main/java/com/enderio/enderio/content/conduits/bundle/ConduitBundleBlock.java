package com.enderio.enderio.content.conduits.bundle;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIODataComponents;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitRedstoneSignalAware;
import com.enderio.enderio.api.conduits.bundle.AddConduitResult;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.client.content.conduits.ConduitBreakParticle;
import com.enderio.enderio.client.content.conduits.model.facades.FacadeUtil;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.conduits.menu.ConduitMenu;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduit;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduitConnectionConfig;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduitNetworkContext;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOConduitTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@EventBusSubscriber
public class ConduitBundleBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ConduitBundleBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ConduitBundleBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        // return createTickerHelper(blockEntityType, typeSupplier.get(),
        // EIOBlockEntity::tick);
        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof ConduitBundleBlockEntity conduitBundleBlockEntity) {
                if (level.isClientSide()) {
                    conduitBundleBlockEntity.clientTick();
                } else {
                    conduitBundleBlockEntity.serverTick();
                }
                conduitBundleBlockEntity.endTick();
            }
        };
    }

    // region Block Shape

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof Player player) {
            return getBundleShape(level, pos, FacadeUtil.areFacadesVisible(player));
        }

        return getBundleShape(level, pos, true);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getBundleShape(level, pos, true);
    }

    private VoxelShape getBundleShape(BlockGetter level, BlockPos pos, boolean areFacadesVisible) {
        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduit) {
            if (conduit.hasFacade() && areFacadesVisible) {
                return Shapes.block();
            }

            // Ensure if a bundle is bugged with 0 conduits that it can be broken.
            if (conduit.getConduits().isEmpty()) {
                return Shapes.block();
            }

            return conduit.getShape().getTotalShape();
        }

        // If there's no block entity, no shape - this will stop a bounding box flash
        // when the bundle is first placed
        return Shapes.empty();
    }

    // endregion

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        var target = player.pick(player.blockInteractionRange(), 0, false);
        if (!(target instanceof BlockHitResult blockHitResult)) {
            return ItemStack.EMPTY;
        }

        if (!blockHitResult.getBlockPos().equals(pos)) {
            // Pass through to the block that the player is actually looking at.
            return level.getBlockState(blockHitResult.getBlockPos())
                .getCloneItemStack(blockHitResult.getBlockPos(), level, includeData, player);
        }

        if (!(level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity blockEntity)) {
            return super.getCloneItemStack(level, pos, state, includeData, player);
        }

        if (blockEntity.hasFacade() && FacadeUtil.areFacadesVisible(player)) {
            return blockEntity.getFacadeBlock().asItem().getDefaultInstance();
        }

        if (blockEntity.getConduits().isEmpty()) {
            return ItemStack.EMPTY;
        }

        Holder<Conduit<?, ?>> conduit = blockEntity.getShape().getConduit(pos, target);
        if (conduit == null) {
            conduit = blockEntity.getConduits().getFirst();
        }

        return ConduitBlockItem.getStackFor(conduit, 1);

    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduit) {
            conduit.updateNeighborRedstone();

            // If a non-conduit neighbor updates, re-scan for connections
            if (neighborBlock != EIOBlocks.CONDUIT_BUNDLE.get()) {
                conduit.updateConnections(level, true);
            }

            // Invalidate caps in case of redstone update or something else.
            level.invalidateCapabilities(pos);
        }

        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
    }

    // region Water-logging

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED,
                context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction,
        BlockPos neighborPos, BlockState neighborState, RandomSource random) {

        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduit) {
            conduit.updateShape();
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // endregion

    // region Place & Destroy Logic

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack) {
        // Try to get placing player.
        Player player = null;
        if (placer instanceof Player) {
            player = (Player) placer;
        }

        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle) {
            Holder<Conduit<?, ?>> conduit = stack.get(EnderIODataComponents.CONDUIT);
            if (conduit != null) {
                // Use the primary connection face, if available from placement.
                Direction primaryConnectionSide = conduitBundle.primaryConnectionSide;
                conduitBundle.primaryConnectionSide = null;

                conduitBundle.addConduit(conduit, primaryConnectionSide, player);
            } else {
                // We might be placed using a facade item. If we are, apply the facade to the
                var facadeProvider = stack.getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER);
                if (facadeProvider != null && facadeProvider.isValid()) {
                    conduitBundle.setFacadeProvider(stack);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleCancelledBlockBreak(BreakBlockEvent event) {
        // If a conduit bundle block break was cancelled, we'll need to send the block entity data back to the clients to correct the visuals.
        if (event.getState().is(EIOBlocks.CONDUIT_BUNDLE) && event.isCanceled()) {
            event.getPlayer().level().sendBlockUpdated(event.getPos(), event.getState(), event.getState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolItem, boolean willHarvest, FluidState fluid) {
        if (!(level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle)) {
            // TODO: Should we allow it to break?
            return false;
        }

        // If this action is going to completely destroy the conduit bundle, we don't want to modify the block entity state at all.
        // If the block break is cancelled by the server, the block will pop back into existence, but the block entity data will be missing (and our event
        //  above will not be able to help).
        // So, we'll just specially case this scenario to ensure rollback can work correctly.
        if (conduitBundle.isEmpty() ||
            (conduitBundle.getConduits().size() == 1 && !conduitBundle.hasFacade()) ||
            (conduitBundle.getConduits().isEmpty() && conduitBundle.hasFacade())) {

            if (level.isClientSide()) {
                if (!conduitBundle.getConduits().isEmpty()) {
                    var conduit = conduitBundle.getConduits().getFirst();
                    ConduitBreakParticle.addDestroyEffects(pos, state, conduit.value());
                } else {
                    // TODO: Facade particles?
                }
            } else {
                if (!player.getAbilities().instabuild) {
                    if (conduitBundle.hasFacade()) {
                        popResource(level, pos, conduitBundle.getFacadeProvider());
                    } else {
                        var lastConduit = conduitBundle.getConduits().getFirst();
                        popResource(level, pos, ConduitBlockItem.getStackFor(lastConduit, 1));
                    }
                }
            }

            // When the block entity is removed, it will drop all necessary resources.
            return super.onDestroyedByPlayer(state, level, pos, player, toolItem, willHarvest, fluid);
        }

        // Remove facade, if visible
        if (conduitBundle.hasFacade() && FacadeUtil.areFacadesVisible(player)) {
            if (!level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    conduitBundle.dropFacadeItem();
                }
            }

            int lightLevelBefore = level.getLightEmission(pos);
            conduitBundle.setFacadeProvider(ItemStack.EMPTY);

            // Handle light update
            if (lightLevelBefore != level.getLightEmission(pos)) {
                level.getLightEngine().checkBlock(pos);
            }
        } else {
            Holder<Conduit<?, ?>> conduit = null;

            // More than 1 conduit, find it and remove it - do not destroy bundle.
            HitResult hit = player.pick(player.blockInteractionRange() + 5, 0.0f, false);
            if (hit.getType() == HitResult.Type.BLOCK) {
                conduit = conduitBundle.getShape().getConduit(((BlockHitResult) hit).getBlockPos(), hit);
            }

            // No hit, no break.
            if (conduit == null) {
                return false;
            }

            if (level.isClientSide()) {
                ConduitBreakParticle.addDestroyEffects(pos, state, conduit.value());
            }

            conduitBundle.removeConduit(conduit, droppedItem -> {
                if (!level.isClientSide() && !player.getAbilities().instabuild) {
                    popResource(level, pos, droppedItem);
                }
            });
        }

        // Ensure we've not left the conduit 'empty' - the short-circuit clause above this should have caught this case
        if (!FMLEnvironment.isProduction() && conduitBundle.isEmpty()) {
            throw new IllegalStateException("Bundle became empty - should not have reached non-empty break logic.");
        }

        return false;
    }

    // endregion

    // region Interactions

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle) {
            var result = addConduit(stack, level, pos, player, conduitBundle);
            if (result != InteractionResult.TRY_WITH_EMPTY_HAND) {
                return result;
            }

            result = addFacade(stack, level, pos, player, conduitBundle);
            if (result != InteractionResult.TRY_WITH_EMPTY_HAND) {
                return result;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private InteractionResult addConduit(ItemStack stack, Level level, BlockPos pos, Player player,
            ConduitBundleBlockEntity conduitBundle) {
        // Get the conduit from the item
        Holder<Conduit<?, ?>> conduit = stack.get(EnderIODataComponents.CONDUIT);
        if (conduit == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!conduitBundle.canAddConduit(conduit)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        // Attempt to add to the bundle
        HitResult hit = player.pick(player.blockInteractionRange() + 5, 1, false);
        Direction primaryConnectionSide = null;
        if (hit instanceof BlockHitResult blockHitResult) {
            primaryConnectionSide = blockHitResult.getDirection().getOpposite();
        }

        AddConduitResult addResult = conduitBundle.addConduit(conduit, primaryConnectionSide, player);

        if (addResult instanceof AddConduitResult.Upgrade(Holder<Conduit<?, ?>> replacedConduit)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                player.getInventory().placeItemBackInInventory(ConduitBlockItem.getStackFor(replacedConduit, 1));
            }
        } else if (addResult instanceof AddConduitResult.Insert) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        } else {
            if (!FMLEnvironment.isProduction()) {
                throw new IllegalStateException(
                        "ConduitBundleAccessor#canAddConduit returned true, but addConduit returned BLOCKED");
            }

            return InteractionResult.FAIL;
        }

        BlockState blockState = level.getBlockState(pos);
        SoundType soundtype = blockState.getSoundType(level, pos, player);
        level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS,
                (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, blockState));

        return InteractionResult.SUCCESS;
    }

    private InteractionResult addFacade(ItemStack stack, Level level, BlockPos pos, Player player,
            ConduitBundleBlockEntity conduitBundle) {
        var facadeProvider = stack.getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER);
        if (facadeProvider == null || !facadeProvider.isValid()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (conduitBundle.hasFacade()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        int lightLevelBefore = level.getLightEmission(pos);

        conduitBundle.setFacadeProvider(stack);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        // Handle light change
        if (lightLevelBefore != level.getLightEmission(pos)) {
            level.getLightEngine().checkBlock(pos);
        }

        // Block place effects
        BlockState blockState = level.getBlockState(pos);
        SoundType soundtype = blockState.getSoundType(level, pos, player);
        level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS,
                (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, blockState));

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle) {
            // TODO: The connection shouldn't include the plate.. if we hit the plate open
            // the first conduit?
            var conduitConnection = conduitBundle.getShape().getConnectionFromHit(pos, hitResult);

            if (conduitConnection != null) {
                if (conduitBundle.canOpenScreen(conduitConnection.getSecond(), conduitConnection.getFirst())) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        ConduitMenu.openConduitMenu(serverPlayer, conduitBundle, conduitConnection.getFirst(), conduitConnection.getSecond());
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    // endregion

    // region Hardcoded Redstone Logic

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
            @Nullable Direction direction) {
        if (direction == null) {
            return false;
        }

        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle) {
            // Get the redstone conduit
            var redstoneConduit = conduitBundle.getConduitByType(EIOConduitTypes.REDSTONE.get());
            if (redstoneConduit == null) {
                return false;
            }

            var status = conduitBundle.getConnectionStatus(redstoneConduit, direction);
            if (status != ConnectionStatus.CONNECTED_BLOCK) {
                return false;
            }

            var config = conduitBundle.getConnectionConfig(redstoneConduit, direction,
                    RedstoneConduitConnectionConfig.TYPE);
            return config.canInsert(ConduitRedstoneSignalAware.NONE);
        }

        return false;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getNetworkSignal(level, pos, direction, false);
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getNetworkSignal(level, pos, direction, true);
    }

    private int getNetworkSignal(BlockGetter level, BlockPos pos, Direction direction, boolean isDirectSignal) {
        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle) {
            // No client redstone, network is server-only.
            if (conduitBundle.getLevel().isClientSide()) {
                return 0;
            }

            // Get the redstone conduit
            var redstoneConduit = conduitBundle.getConduitByType(EIOConduitTypes.REDSTONE.get());
            if (redstoneConduit == null) {
                return 0;
            }

            var status = conduitBundle.getConnectionStatus(redstoneConduit, direction.getOpposite());
            if (status != ConnectionStatus.CONNECTED_BLOCK) {
                return 0;
            }

            var config = conduitBundle.getConnectionConfig(redstoneConduit, direction.getOpposite(),
                    RedstoneConduitConnectionConfig.TYPE);
            if (!config.canInsert(ConduitRedstoneSignalAware.NONE)) {
                return 0;
            }

            if (isDirectSignal && !config.isStrongOutputSignal()) {
                return 0;
            }

            var node = conduitBundle.getConduitNode(redstoneConduit);
            var network = node.getNetwork();
            if (network == null) {
                return 0;
            }

            var context = network.getContext(RedstoneConduitNetworkContext.TYPE);
            if (context == null) {
                return 0;
            }

            var redstoneInsertFilter = node.getInventory(direction.getOpposite())
                    .getStackInSlot(RedstoneConduit.INSERT_FILTER_SLOT)
                    .getCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER);

            if (redstoneInsertFilter != null) {
                return redstoneInsertFilter.getOutputSignal(context, config.insertChannel());
            }

            return context.getSignal(config.insertChannel());
        }

        return 0;
    }

    // endregion

    // region Facade Behaviours

    private Optional<Block> getFacadeBlock(BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle) {
            if (conduitBundle.hasFacade()) {
                return Optional.of(conduitBundle.getFacadeBlock());
            }
        }

        return Optional.empty();
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndLightGetter level, BlockPos pos, Direction side,
            @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        Optional<Block> facade = getFacadeBlock(level, pos);
        return facade.map(Block::defaultBlockState).orElseGet(() -> super.getAppearance(state, level, pos, side, queryState, queryPos));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        Optional<Block> facade = getFacadeBlock(level, pos);
        return facade.map(block -> block.getLightEmission(block.defaultBlockState(), level, pos)).orElseGet(() -> super.getLightEmission(state, level, pos));
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        Optional<Block> facade = getFacadeBlock(level, pos);
        return facade
            .map(block -> block.getFriction(block.defaultBlockState(), level, pos, entity))
            .orElseGet(() -> super.getFriction(state, level, pos, entity));
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        Optional<Block> facade = getFacadeBlock(level, pos);
        if (facade.isPresent()) {
            if (!(entity instanceof Player) || FacadeUtil.areFacadesVisible((Player) entity)) {
                return facade.get().getSoundType(facade.get().defaultBlockState(), level, pos, entity);
            }
        }

        return super.getSoundType(state, level, pos, entity);
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    // endregion
}
