package com.enderio.machines.common.blocks.fluid_tank;

import com.enderio.machines.common.blocks.base.block.MachineBlock;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FluidTankBlock extends MachineBlock<FluidTankBlockEntity> {
    public FluidTankBlock(Supplier<BlockEntityType<? extends FluidTankBlockEntity>> blockEntityType,
            Properties properties) {
        super(blockEntityType, properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!stack.isEmpty()) {
            if (level.getBlockEntity(pos) instanceof FluidTankBlockEntity tank) {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }

                if (tank.handleFluidItemInteraction(player, hand, stack, tank, FluidTankBlockEntity.TANK)) {
                    player.getInventory().setChanged();
                    return ItemInteractionResult.CONSUME;
                }
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof FluidTankBlockEntity tank) {
            return FluidTankBlockEntity.TANK.getFluid(tank).getFluid().getFluidType().getLightLevel();
        }

        return super.getLightEmission(state, level, pos);
    }
}
