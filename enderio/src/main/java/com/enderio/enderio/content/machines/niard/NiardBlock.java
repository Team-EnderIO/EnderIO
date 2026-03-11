package com.enderio.enderio.content.machines.niard;

import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class NiardBlock extends ProgressMachineBlock<NiardBlockEntity> {

    public NiardBlock(Properties properties) {
        super(EIOBlockEntities.NIARD::get, properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hitResult) {

        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            if (level.getBlockEntity(pos) instanceof NiardBlockEntity tank) {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }

                if (tank.handleFluidItemInteraction(player, hand, stack, tank, NiardBlockEntity.TANK)) {
                    player.getInventory().setChanged();
                    return InteractionResult.CONSUME;
                }
            }
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }
}
