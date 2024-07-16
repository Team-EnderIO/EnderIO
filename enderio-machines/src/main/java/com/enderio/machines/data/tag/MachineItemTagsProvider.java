package com.enderio.machines.data.tag;

import com.enderio.EnderIOBase;
import com.enderio.machines.common.tag.MachineTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class MachineItemTagsProvider extends ItemTagsProvider {

    public MachineItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, EnderIOBase.REGISTRY_NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MachineTags.ItemTags.EXPLOSIVES).add(Items.TNT, Items.FIREWORK_STAR, Items.FIREWORK_ROCKET);
        tag(MachineTags.ItemTags.NATURAL_LIGHTS).add(Items.GLOWSTONE_DUST, Items.GLOWSTONE, Items.SEA_LANTERN, Items.SEA_PICKLE, Items.GLOW_LICHEN, Items.GLOW_BERRIES, Items.GLOW_INK_SAC);
        tag(MachineTags.ItemTags.SUNFLOWER).add(Items.SUNFLOWER);
        tag(MachineTags.ItemTags.BLAZE_POWDER).add(Items.BLAZE_POWDER);
    }
}
