package com.enderio.conduits.common.items;

import com.enderio.api.conduit.IConduitType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ConduitBlockItem extends BlockItem {

    public ConduitBlockItem(IConduitType type, Block block, Properties properties) {
        super(block, properties);
    }

    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

}
