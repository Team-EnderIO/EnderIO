package com.enderio.conduits.common;

import com.enderio.EnderIO;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ConduitTags {

    public static final TagKey<Block> REDSTONE_CONNECTABLE = BlockTags.create(EnderIO.loc("redstone_connectable"));

    public static void register() {
    }
}