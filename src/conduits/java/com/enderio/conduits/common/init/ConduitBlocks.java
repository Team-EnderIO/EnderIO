package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.common.blocks.ConduitBlock;
import com.enderio.conduits.data.model.ConduitBlockState;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.MapColor;

public class ConduitBlocks {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntry<ConduitBlock> CONDUIT = REGISTRATE
        .block("conduit", ConduitBlock::new)
        .properties(props -> props.strength(1.5f, 10).noLootTable().noOcclusion().dynamicShape().mapColor(MapColor.STONE))
        .blockstate(ConduitBlockState::conduit)
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .register();


    public static void register() {}
}
