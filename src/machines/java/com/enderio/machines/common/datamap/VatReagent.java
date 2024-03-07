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

    public static final AdvancedDataMapType<Item, Map<TagKey<Item>, Double>, DataMapValueRemover.Default<Map<TagKey<Item>, Double>, Item>> DATA_MAP = AdvancedDataMapType
        .builder(EnderIO.loc("vat_reagent"), Registries.ITEM, ExtraCodecs.strictUnboundedMap(TagKey.hashedCodec(Registries.ITEM), Codec.DOUBLE))
        .build();

    //    public record ReagentType(double modifier) {
    //        public static final Codec<ReagentType> CODEC = RecordCodecBuilder.create(
    //            in -> in.group(Codec.DOUBLE.fieldOf("modifier").forGetter(ReagentType::modifier)).apply(in, ReagentType::new));
    //    }
}
