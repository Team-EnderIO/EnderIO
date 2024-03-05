package com.enderio.machines.common.datamap;

import com.enderio.EnderIO;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;

import java.util.Map;

public class VatReagent {

    public static final AdvancedDataMapType<Item, Map<TagKey<Item>, ReagentType>, DataMapValueRemover.Default<Map<TagKey<Item>, ReagentType>, Item>> DATA_MAP = AdvancedDataMapType
        .builder(EnderIO.loc("vat_reagent"), Registries.ITEM, ExtraCodecs.strictUnboundedMap(TagKey.hashedCodec(Registries.ITEM), ReagentType.CODEC))
        .build();

    public record ReagentType(double modifier) {
        public static final Codec<ReagentType> CODEC = RecordCodecBuilder.create(
            in -> in.group(Codec.DOUBLE.fieldOf("modifier").forGetter(ReagentType::modifier)).apply(in, ReagentType::new));
    }
}
