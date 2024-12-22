package com.enderio.machines.common.blocks.vat;

import com.enderio.machines.common.blocks.base.block.ProgressMachineBlock;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class VatBlock extends ProgressMachineBlock<VatBlockEntity> {
    public VatBlock(RegiliteBlockEntity<? extends VatBlockEntity> blockEntityType, Properties properties) {
        super(blockEntityType, properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!stack.isEmpty()) {
            if (level.getBlockEntity(pos) instanceof VatBlockEntity vat) {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }

                if (vat.handleFluidItemInteraction(player, hand, stack, vat, VatBlockEntity.INPUT_TANK)
                        || vat.handleFluidItemInteraction(player, hand, stack, vat, VatBlockEntity.OUTPUT_TANK)) {
                    player.getInventory().setChanged();
                    return ItemInteractionResult.CONSUME;
                }
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
