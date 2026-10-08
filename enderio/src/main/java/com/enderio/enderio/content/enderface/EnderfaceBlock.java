package com.enderio.enderio.content.enderface;

import com.enderio.enderio.foundation.block.EIOEntityBlock;
import com.enderio.enderio.init.EIOBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class EnderfaceBlock extends EIOEntityBlock<EnderfaceBlockEntity> {
    public EnderfaceBlock(Properties properties) {
        super(EIOBlockEntities.ENDERFACE::get, properties);
    }
}
