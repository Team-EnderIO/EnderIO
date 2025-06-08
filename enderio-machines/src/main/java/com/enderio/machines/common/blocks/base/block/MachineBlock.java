package com.enderio.machines.common.blocks.base.block;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.binding.ISoulBindable;
import com.enderio.base.api.soul.storage.ISoulHandler;
import com.enderio.base.common.block.EIOEntityBlock;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public class MachineBlock<T extends MachineBlockEntity> extends EIOEntityBlock<T> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public MachineBlock(Supplier<BlockEntityType<? extends T>> typeSupplier, Properties properties) {
        super(typeSupplier, properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new NotImplementedException("Block codecs are a later problem...");
    }

    /**
     * Override this if you have a machine with no menu.
     */
    protected boolean canOpenMenu() {
        return true;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        // Do not allow opening in spectator mode.
        // TODO: We can convert our menus to not use a BE backing fully to enable this.
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {

        // Attempt to open machine menu.
        if (canOpenMenu()) {
//            var menuProvider = this.getMenuProvider(state, level, pos);
            if (level.getBlockEntity(pos) instanceof MenuProvider menuProvider) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(menuProvider, pos);
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player player && level.getBlockEntity(pos) instanceof MachineBlockEntity machine) {
            machine.setMachineOwner(player.getUUID());
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult hitResult) {
        if (!player.getAbilities().instabuild) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        ISoulBindable soulBindable = level.getCapability(EIOCapabilities.SoulBindable.BLOCK, pos);
        if (soulBindable != null && soulBindable.canBind()) {
            ISoulHandler soulHandler = stack.getCapability(EIOCapabilities.SoulHandler.ITEM);
            if (soulHandler != null) {
                for (int i = 0; i < soulHandler.getSlots(); i++) {
                    Soul soul = soulHandler.getSoulInSlot(i);
                    if (soulBindable.isSoulValid(soul)) {
                        soulBindable.bindSoul(soul.copy());
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
