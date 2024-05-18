package com.enderio.machines.common.block;

import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MachineBlock extends BaseEntityBlock {
    public static final Codec<Supplier<BlockEntityType<? extends MachineBlockEntity>>> BLOCK_ENTITY_TYPE_CODEC = BuiltInRegistries.BLOCK_ENTITY_TYPE
        .holderByNameCodec()
        .flatXmap(blockEntityTypeHolder -> DataResult.success(() -> (BlockEntityType<? extends MachineBlockEntity>) blockEntityTypeHolder.value()),
            sup -> DataResult.success(sup.get().builtInRegistryHolder()));

    private static final MapCodec<MachineBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BLOCK_ENTITY_TYPE_CODEC.fieldOf("block_entity_type").forGetter(output -> output.blockEntityType),
        propertiesCodec()
    ).apply(instance, MachineBlock::new));

    private final Supplier<BlockEntityType<? extends MachineBlockEntity>> blockEntityType;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private MachineBlock(Supplier<BlockEntityType<? extends MachineBlockEntity>> blockEntityType, Properties properties) {
        super(properties);

        this.blockEntityType = blockEntityType;
        BlockState any = this.getStateDefinition().any();
        this.registerDefaultState(any.hasProperty(FACING) ? any.setValue(FACING, Direction.NORTH) : any);
    }

    public MachineBlock(RegiliteBlockEntity<? extends MachineBlockEntity> blockEntityType, Properties properties) {
        super(properties);
        this.blockEntityType = blockEntityType::get;
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

    @Override
    protected MapCodec<? extends MachineBlock> codec() {
        return CODEC;
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

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
        InteractionHand interactionHand, BlockHitResult hit) {

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof MachineBlockEntity machineBlockEntity)) { // This also covers nulls
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.is(EIOTags.Items.WRENCH)) {
            var res = machineBlockEntity.onWrenched(player, hit.getDirection());
            if (res != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
                return res;
            }
        }

        var result = machineBlockEntity.onBlockEntityUsed(state, level, pos, player, interactionHand, hit);
        if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
            return result;
        }

        return super.useItemOn(stack, state, level, pos, player, interactionHand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide()){
            return InteractionResult.SUCCESS;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof MachineBlockEntity machineBlockEntity)) { // This also covers nulls
            return InteractionResult.PASS;
        }

        if (!machineBlockEntity.canOpenMenu()) {
            return InteractionResult.PASS;
        }

        MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
        if (menuprovider != null && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(menuprovider, buf -> buf.writeBlockPos(pos));
        }

        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return blockEntityType.get().create(pPos, pState);
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
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MachineBlockEntity machineBlock) {
            return machineBlock.getLightEmission();
        }
        return super.getLightEmission(state, level, pos);
    }
}
