package com.enderio.enderio.content.machines.killer_joe;

import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class KillerJoeBlock extends ProgressMachineBlock<KillerJoeBlockEntity> {
    public KillerJoeBlock(Properties properties) {
        super(EIOBlockEntities.KILLER_JOE::get, properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!stack.isEmpty()) {
            if (level.getBlockEntity(pos) instanceof KillerJoeBlockEntity killerJoe) {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }

                // Allow fluid interaction with buckets
                if (killerJoe.handleFluidItemInteraction(player, hand, stack, killerJoe, KillerJoeBlockEntity.TANK)) {
                    player.getInventory().setChanged();
                    return ItemInteractionResult.CONSUME;
                }
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
