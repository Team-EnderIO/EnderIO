package com.enderio.enderio.api.armory;

import com.enderio.enderio.api.EnderIOAPI;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;

import java.util.List;

public class MobHeadDrop {
    public static final Codec<List<ItemStack>> CODEC = Codec.list(ItemStack.OPTIONAL_CODEC, 1, 255);

    public static final AdvancedDataMapType<EntityType<?>, List<ItemStack>, DataMapValueRemover.Default<List<ItemStack>, EntityType<?>>> DATA_MAP =
        AdvancedDataMapType
            .builder(EnderIOAPI.rl("mob_head_drop"), Registries.ENTITY_TYPE, CODEC)
            .merger(DataMapValueMerger.listMerger())
            .build();
}
