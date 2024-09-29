package com.enderio.base.api.capacitor;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;

/**
 * Capacitor key types, for use in loot capacitors for targeting increases to general stats.
 */
public enum CapacitorModifier implements StringRepresentable {
    ENERGY_CAPACITY(1),
    ENERGY_USE(2),
    FUEL_EFFICIENCY(3),
    BURNING_ENERGY_GENERATION(4),

    /**
     * This should always go last as the loot picker will exclude the final item in this enum
     * @apiNote Capacitors should never multiply the FIXED modifiers...
     */
    FIXED(0);

    public static final Codec<CapacitorModifier> CODEC = StringRepresentable.fromEnum(CapacitorModifier::values);
    public static final IntFunction<CapacitorModifier> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, CapacitorModifier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    public final ResourceLocation modifierId;

    public static final List<CapacitorModifier> SELECTABLE_MODIFIERS = List.of(
        ENERGY_CAPACITY,
        ENERGY_USE,
        FUEL_EFFICIENCY,
        BURNING_ENERGY_GENERATION
    );

    public static CapacitorModifier getRandomModifier(RandomSource randomSource) {
        return CapacitorModifier.SELECTABLE_MODIFIERS.get(randomSource.nextInt(CapacitorModifier.SELECTABLE_MODIFIERS.size()));
    }

    CapacitorModifier(int id) {
        this.id = id;
        this.modifierId = ResourceLocation.fromNamespaceAndPath("enderio", "capacitor." + name().toLowerCase(Locale.ROOT));
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
