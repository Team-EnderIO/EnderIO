package com.enderio.base.common.item.misc;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class HangGliderItem extends Item /* implements Equipable */ {

    public HangGliderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos());
        Player player = pContext.getPlayer();
//        if (player != null
//            && blockState.is(Blocks.WATER_CAULDRON)
//            && blockState.getBlock() instanceof LayeredCauldronBlock
//            && EIOItems.COLORED_HANG_GLIDERS.values().stream().map(RegistryEntry::get).toList().contains(this)) {
//
//            player.awardStat(Stats.CLEAN_ARMOR);
//            player.setItemInHand(pContext.getHand(), ItemUtils.createFilledResult(pContext.getItemInHand(), player, EIOItems.GLIDER.asStack()));
//            LayeredCauldronBlock.lowerFillLevel(blockState, pContext.getLevel(), pContext.getClickedPos()); pContext.getItemInHand().shrink(1);
//            return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide());
//        }
        return super.useOn(pContext);
    }

    /*
     * @Override public EquipmentSlot getEquipmentSlot() { return
     * EquipmentSlot.CHEST; }
     *
     * @Nullable public Holder<SoundEvent> getEquipSound() { return
     * SoundEvents.ARMOR_EQUIP_LEATHER; }
     */
}
