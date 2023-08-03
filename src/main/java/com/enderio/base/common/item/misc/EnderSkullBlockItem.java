package com.enderio.base.common.item.misc;

import com.enderio.base.common.init.EIOBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;

public class EnderSkullBlockItem extends StandingAndWallBlockItem {

    public EnderSkullBlockItem(Block block, Properties properties, Direction attachmentDirection) {
        super(block, EIOBlocks.WALL_ENDERMAN_HEAD.get(), properties, attachmentDirection);
    }
}
