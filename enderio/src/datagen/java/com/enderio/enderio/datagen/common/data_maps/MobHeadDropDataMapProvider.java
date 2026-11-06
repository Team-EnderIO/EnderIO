package com.enderio.enderio.datagen.common.data_maps;

import com.enderio.enderio.api.armory.MobHeadDrop;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MobHeadDropDataMapProvider extends DataMapProvider {
    public MobHeadDropDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void gather(HolderLookup.Provider registries) {
        var builder = builder(MobHeadDrop.DATA_MAP);
        builder.add(EntityTypeTags.ZOMBIES, List.of(new ItemStack(Items.ZOMBIE_HEAD)), false);
        builder.add(EntityTypeTags.SKELETONS, List.of(new ItemStack(Items.SKELETON_SKULL)), false);
        builder.add(EntityType.WITHER_SKELETON.builtInRegistryHolder(), List.of(new ItemStack(Items.WITHER_SKELETON_SKULL)), false);
        builder.add(EntityType.CREEPER.builtInRegistryHolder(), List.of(new ItemStack(Items.CREEPER_HEAD)), false);
        builder.add(EntityType.ENDER_DRAGON.builtInRegistryHolder(), List.of(new ItemStack(Items.DRAGON_HEAD)), false);
        builder.add(EntityType.ENDERMAN.builtInRegistryHolder(), List.of(new ItemStack(EIOBlocks.ENDERMAN_HEAD)), false);
        builder.add(EntityType.PIGLIN.builtInRegistryHolder(), List.of(new ItemStack(Items.PIGLIN_HEAD)), false);
        builder.add(EntityType.PIGLIN_BRUTE.builtInRegistryHolder(), List.of(new ItemStack(Items.PIGLIN_HEAD)), false);
        // Override the zombies tag.
        builder.add(EntityType.ZOMBIFIED_PIGLIN.builtInRegistryHolder(), List.of(new ItemStack(Items.PIGLIN_HEAD)), true);
    }

    @Override
    public String getName() {
        return "Mob Head Drops Datamaps";
    }
}
