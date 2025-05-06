package com.enderio.machines.common.datamap;

import com.enderio.base.api.EnderIO;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;

public class RangeExtender {
    public static final ExtraCodecs.StrictUnboundedMapCodec<TagKey<Block>, Integer> CODEC = ExtraCodecs
            .strictUnboundedMap(TagKey.hashedCodec(Registries.BLOCK), Codec.INT);

    public static final AdvancedDataMapType<Block, Map<TagKey<Block>, Integer>, DataMapValueRemover.Default<Map<TagKey<Block>, Integer>, Block>> DATA_MAP = AdvancedDataMapType
            .builder(EnderIO.loc("range_extender"), Registries.BLOCK, CODEC)
            .synced(CODEC, true)
            .build();
}
