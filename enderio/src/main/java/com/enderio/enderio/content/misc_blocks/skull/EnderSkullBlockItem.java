package com.enderio.enderio.content.misc_blocks.skull;

import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;

public class EnderSkullBlockItem extends StandingAndWallBlockItem {

    public EnderSkullBlockItem(Block block, Direction attachmentDirection, Properties properties) {
        super(block, EIOBlocks.WALL_ENDERMAN_HEAD.get(), attachmentDirection, properties);
    }
}
