package com.enderio.base.common.block;

import com.enderio.EnderIOBase;
import com.enderio.base.api.soul.Soul;
import com.enderio.base.common.blockentity.SoulPotBlockEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.tool.SoulVialItem;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.particle.SoulParticleData;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.common.soulpot.OriginContext;
import com.enderio.machines.common.soulpot.SoulEnvironmentData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID)
public class SoulCatchingPotBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final EnumProperty<State> CATCHING_PROPERTY = EnumProperty.create("catching", State.class);
    private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final int MAX_POTS_IN_AREA = 3;


    private static final Method getDeathSound = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "getDeathSound");
    private static final Method getSoundVolume = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "getSoundVolume");
    private static final Method getVoicePitch = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "getVoicePitch");

    public SoulCatchingPotBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(CATCHING_PROPERTY, State.CATCHING0).setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED, CATCHING_PROPERTY, HORIZONTAL_FACING);

    }


    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(EIOBlocks.SOUL_POT.get(), new SoulCatchingPotBlock.SoulPotPlaceBehavior());
        });
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return EIOBlockEntities.SOUL_POT.create(blockPos, blockState);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        State catchingState = state.getValue(CATCHING_PROPERTY);
        if (catchingState != State.CAUGHT) {
            int soulPots =  countPots(level, pos);
            Interference interference = soulOvercrowdedness(soulPots, random);
            if (soulPots <= MAX_POTS_IN_AREA || interference == Interference.CONTINUE) {
                catchingState = catchingState.next();
                level.setBlock(pos, state.setValue(CATCHING_PROPERTY, catchingState), Block.UPDATE_ALL);
                if (catchingState == State.CAUGHT && level.getBlockEntity(pos) instanceof SoulPotBlockEntity soulPotBlockEntity) {
                    Soul soul = soulPotBlockEntity.catchEntity();
                    if (soul != null && soul.hasEntity()) {

                        level.updateNeighbourForOutputSignal(pos, state.getBlock());
                        Optional<Entity> entityOpt = EntityType.create(soul.getEntityTag(), level);
                        if (entityOpt.isPresent() && entityOpt.get() instanceof LivingEntity entity) {
                            try {
                                ((LevelAccessor)level).playSound(null, pos, (SoundEvent) getDeathSound.invoke(entity), SoundSource.BLOCKS, (float)getSoundVolume.invoke(entity), (float)getVoicePitch.invoke(entity));

                            } catch (IllegalAccessException | InvocationTargetException e) {

                            }
                        }
                    }
                }
            } else if (Interference.BREAK == interference) {
                level.destroyBlock(pos, false);
            }
        }
        super.randomTick(state, level, pos, random);
    }

    private int countPots(Level level, BlockPos pos) {
        return (int) level.getBlockStatesIfLoaded(new AABB(pos).inflate(2)).filter(s -> s.is(this)).count();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(CATCHING_PROPERTY) == State.CAUGHT) {
            player.displayClientMessage(EIOLang.CAUGHT, true);
        } else {
            Optional<EntityType<?>> entity = SoulEnvironmentData.findEntity(level.random, new OriginContext(level, pos));
            if (entity.isPresent()) {
                if (countPots(level, pos) > MAX_POTS_IN_AREA) {
                    player.displayClientMessage(EIOLang.TOO_MANY_POTS, true);
                } else {
                    player.displayClientMessage(EIOLang.CATCHING, true);
                }
            } else {
                player.displayClientMessage(EIOLang.NO_SOULS, true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    Interference soulOvercrowdedness(int countPots, RandomSource random) {
        int r = random.nextInt(100);
        if (r <= countPots)
            return Interference.BREAK;
        if (r <= countPots*5)
            return Interference.PASS;
        return Interference.CONTINUE;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(CATCHING_PROPERTY) != State.CAUGHT;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.is(this)) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }
        ItemStack soulVial;
        if (level.getBlockEntity(pos) instanceof SoulPotBlockEntity soulPot && soulPot.getCaughtEntity() != null) {
            soulVial = SoulVialItem.forSoul(soulPot.getCaughtEntity());
        } else {
            soulVial = EIOItems.SOUL_VIAL.toStack();
        }
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), soulVial);
        super.onRemove(state, level, pos, newState, movedByPiston);

        level.updateNeighbourForOutputSignal(pos, state.getBlock());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_272711_) {
        FluidState fluidstate = p_272711_.getLevel().getFluidState(p_272711_.getClickedPos());
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, p_272711_.getHorizontalDirection())
            .setValue(BlockStateProperties.WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(CATCHING_PROPERTY) == State.CAUGHT)
            return;
        BlockPos[] candidates = collectPositions(pos, level);
        if (candidates.length == 0)
            return;
        createParticle(pos, candidates[random.nextInt(candidates.length)], level);
    }

    private static BlockPos[] collectPositions(BlockPos potPos, Level level) {
        return BlockPos
            .betweenClosedStream(AABB.ofSize(potPos.getCenter(), 7, 7, 7))
            .filter(candidate -> level.getBlockState(candidate).isSolid())
            .filter(candidate -> isExposed(level, candidate))
            .map(BlockPos::immutable)
            .toArray(BlockPos[]::new);
    }

    private static void createParticle(BlockPos potPos, BlockPos sourceBlock, Level level) {
        level.addParticle(
            new SoulParticleData(potPos, sourceBlock),
                sourceBlock.getX(),
                sourceBlock.getY(),
                sourceBlock.getZ(),
            0.0, 0.0, 0.0);
    }

    private static boolean isExposed(Level level, BlockPos pos) {
        for (Direction dir: Direction.values()) {
            if (!level.getBlockState(pos.relative(dir)).isSolid()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SoulPotBlockEntity soulPot && soulPot.getCaughtEntity() != null) {
            return 15;
        } else {
            return 0;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var soul = stack.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY);
        if (!soul.isEmpty()) {
            tooltipComponents.add(TooltipUtil.style(Component.translatable(soul.entityType().getDescriptionId())));
        }
    }

    private static class SoulPotPlaceBehavior extends OptionalDispenseItemBehavior {
        @Override
        protected ItemStack execute(BlockSource blockSource, ItemStack item) {
            Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
            BlockPos targetPos = blockSource.pos().relative(direction);
            if (blockSource.level().getBlockState(targetPos).canBeReplaced()) {
                try {
                    this.setSuccess(
                        ((BlockItem)item.getItem()).place(new DirectionalPlaceContext(blockSource.level(), targetPos, direction, item, direction.getOpposite())).consumesAction()
                    );
                } catch (Exception exception) {
                    LOGGER.error("Error trying to place soul pot at {}", targetPos, exception);
                }
            }
            return item;
        }
    }

    public enum State implements StringRepresentable {
        CATCHING0, CATCHING1, CATCHING2, CAUGHT;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public State next() {
            return switch (this) {
                case CATCHING0 -> CATCHING1;
                case CATCHING1 -> CATCHING2;
                case CATCHING2 -> CAUGHT;
                case CAUGHT -> throw new IllegalStateException("tried to get the state after caught");
            };
        }
    }

    enum Interference {
        CONTINUE,//allow soul catching
        PASS,//disallow soul catching
        BREAK; // break soul pot to prevent soul pot spam
    }
}
