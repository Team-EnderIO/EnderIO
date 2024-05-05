package com.enderio.base.common.capacitor;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.ICapacitorData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

// TODO: Instead of loot capacitors having lists of specialized machines, have different loot capacitor items for different
//       machine categories.
// TODO: Loot capacitor types (Sculk, Soul) found in respective dungeons/structures.

// TODO: Rename to CapacitorData
public record LootCapacitorData(float base, Map<CapacitorModifier, Float> modifiers) implements ICapacitorData {

    public static final Codec<Map<CapacitorModifier, Float>> MODIFIER_CODEC = Codec.unboundedMap(CapacitorModifier.CODEC, Codec.FLOAT);

    public static final Codec<LootCapacitorData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.FLOAT.fieldOf("base").forGetter(LootCapacitorData::getBase),
            MODIFIER_CODEC.fieldOf("specializations").forGetter(LootCapacitorData::getAllModifiers)
        ).apply(instance, LootCapacitorData::new)
    );

    public static final StreamCodec<FriendlyByteBuf, Map<CapacitorModifier, Float>> MODIFIER_STREAM_CODEC
        = ByteBufCodecs.map(HashMap::new, CapacitorModifier.STREAM_CODEC, ByteBufCodecs.FLOAT);

    public static final StreamCodec<FriendlyByteBuf, LootCapacitorData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        LootCapacitorData::getBase,
        MODIFIER_STREAM_CODEC,
        LootCapacitorData::getAllModifiers,
        LootCapacitorData::new
    );

    public static LootCapacitorData simple(float base) {
        return new LootCapacitorData(base, Map.of());
    }

    @Override
    public float getBase() {
        return base;
    }

    @Override
    public float getModifier(CapacitorModifier modifier) {
        if (modifiers.containsKey(modifier)) {
            return modifiers.get(modifier);
        }

        return getBase();
    }

    @Override
    public Map<CapacitorModifier, Float> getAllModifiers() {
        return modifiers;
    }
}
