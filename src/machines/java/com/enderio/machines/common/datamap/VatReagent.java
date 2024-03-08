package com.enderio.machines.common.datamap;

import com.enderio.EnderIO;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;

import java.util.Map;

public class VatReagent {
    public static final ExtraCodecs.StrictUnboundedMapCodec<TagKey<Item>, Double> CODEC = ExtraCodecs.strictUnboundedMap(TagKey.hashedCodec(Registries.ITEM),
        Codec.DOUBLE);

    public static final AdvancedDataMapType<Item, Map<TagKey<Item>, Double>, DataMapValueRemover.Default<Map<TagKey<Item>, Double>, Item>> DATA_MAP = AdvancedDataMapType
        .builder(EnderIO.loc("vat_reagent"), Registries.ITEM, CODEC)
        .synced(CODEC, true)
        .build();

}
