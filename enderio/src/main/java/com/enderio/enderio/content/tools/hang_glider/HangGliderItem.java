package com.enderio.enderio.content.tools.hang_glider;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class HangGliderItem extends Item /* implements Equipable */ {

    public HangGliderItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        Player player = context.getPlayer();
//        if (player != null
//            && blockState.is(Blocks.WATER_CAULDRON)
//            && blockState.getBlock() instanceof LayeredCauldronBlock
//            && EIOItems.COLORED_HANG_GLIDERS.values().stream().map(RegistryEntry::get).toList().contains(this)) {
//
//            player.awardStat(Stats.CLEAN_ARMOR);
//            player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(context.getItemInHand(), player, EIOItems.GLIDER.asStack()));
//            LayeredCauldronBlock.lowerFillLevel(blockState, context.getLevel(), context.getClickedPos()); context.getItemInHand().shrink(1);
//            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
//        }
        return super.useOn(context);
    }

    /*
     * @Override public EquipmentSlot getEquipmentSlot() { return
     * EquipmentSlot.CHEST; }
     *
     * @Nullable public Holder<SoundEvent> getEquipSound() { return
     * SoundEvents.ARMOR_EQUIP_LEATHER; }
     */
}
