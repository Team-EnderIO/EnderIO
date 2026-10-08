package com.enderio.enderio.content.paint.item;

import com.enderio.enderio.content.paint.PaintUtils;
import com.enderio.enderio.content.paint.block.entity.PaintedBlockEntity;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class PaintedBlockItem extends BlockItem {

    public PaintedBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player player) {
        return PaintUtils.getPlaceSound(state, level, pos, player, PaintedBlockItem.class)
            .orElseGet(() -> super.getPlaceSound(state, level, pos, player));
    }
}
