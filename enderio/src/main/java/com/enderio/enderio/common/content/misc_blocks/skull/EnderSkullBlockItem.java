package com.enderio.enderio.common.content.misc_blocks.skull;

import com.enderio.enderio.common.init.EIOBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;

public class EnderSkullBlockItem extends StandingAndWallBlockItem {

    public EnderSkullBlockItem(Block block, Properties properties, Direction attachmentDirection) {
        super(block, EIOBlocks.WALL_ENDERMAN_HEAD.get(), properties, attachmentDirection);
    }
}
