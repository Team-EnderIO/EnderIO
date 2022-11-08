package com.enderio.conduits.data;

import com.enderio.EnderIO;
import com.enderio.conduits.common.ConduitTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

public class ConduitTagProvider extends TagsProvider<Block> {
    public ConduitTagProvider(GatherDataEvent event) {
        super(event.getGenerator(), Registry.BLOCK, EnderIO.MODID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags() {
        tag(ConduitTags.REDSTONE_CONNECTABLE)
            .add(Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.REDSTONE_LAMP, Blocks.NOTE_BLOCK, Blocks.DISPENSER, Blocks.DROPPER,
                Blocks.POWERED_RAIL, Blocks.ACTIVATOR_RAIL)
            .addTags(BlockTags.DOORS, BlockTags.TRAPDOORS, BlockTags.REDSTONE_ORES);
    }
}
