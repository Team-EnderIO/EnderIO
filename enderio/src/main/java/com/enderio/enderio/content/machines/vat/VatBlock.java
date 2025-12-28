package com.enderio.enderio.content.machines.vat;

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

public final class VatBlock extends ProgressMachineBlock<VatBlockEntity> {
    public VatBlock(Properties properties) {
        super(EIOBlockEntities.VAT::get, properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!stack.isEmpty()) {
            if (level.getBlockEntity(pos) instanceof VatBlockEntity vat) {
                if (level.isClientSide()) {
                    return InteractionResult.SUCCESS;
                }

                if (vat.handleFluidItemInteraction(player, hand, stack, vat, VatBlockEntity.INPUT_TANK)
                        || vat.handleFluidItemInteraction(player, hand, stack, vat, VatBlockEntity.OUTPUT_TANK)) {
                    player.getInventory().setChanged();
                    return InteractionResult.CONSUME;
                }
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
