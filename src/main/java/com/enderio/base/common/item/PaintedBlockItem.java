package com.enderio.base.common.item;

import com.enderio.base.common.util.PaintUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class PaintedBlockItem extends BlockItem {

    public PaintedBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player player) {
        return PaintUtils.getPlaceSound(state, level, pos, player, PaintedBlockItem.class)
            .orElseGet(() -> super.getPlaceSound(state, level, pos, player));
    }
}
