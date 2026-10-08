package com.enderio.enderio.api.soul.ingredient;

import com.enderio.enderio.api.soul.Soul;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.HolderSetCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Stream;

@ApiStatus.Experimental
@ApiStatus.AvailableSince("10.0.0")
public class SimpleSoulIngredient extends SoulIngredient {

    private static final Codec<HolderSet<EntityType<?>>> ENTITY_TYPE_HOLDER_SET = HolderSetCodec.create(Registries.ENTITY_TYPE,
        BuiltInRegistries.ENTITY_TYPE.holderByNameCodec(), false);

    static final Codec<SimpleSoulIngredient> CONTENTS_CODEC = ExtraCodecs.nonEmptyHolderSet(ENTITY_TYPE_HOLDER_SET)
        .xmap(SimpleSoulIngredient::new, SimpleSoulIngredient::entityTypeSet);

    private static final StreamCodec<RegistryFriendlyByteBuf, SimpleSoulIngredient> CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ENTITY_TYPE)
        .map(SimpleSoulIngredient::new, SimpleSoulIngredient::entityTypeSet);

    private static final MapCodec<SimpleSoulIngredient> CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }

        @Override
        public <T> DataResult<SimpleSoulIngredient> decode(DynamicOps<T> ops, MapLike<T> input) {
            return DataResult.error(() -> "Simple soul ingredients cannot be decoded using map syntax!");
        }

        @Override
        public <T> RecordBuilder<T> encode(SimpleSoulIngredient input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return prefix.withErrorsFrom(DataResult.error(() -> "Simple soul ingredients cannot be encoded using map syntax! Please use vanilla syntax (namespaced:item or #tag) instead!"));
        }
    };

    public static final SoulIngredientType<SimpleSoulIngredient> TYPE = new SoulIngredientType<>(CODEC, CONTENTS_STREAM_CODEC);

    private final HolderSet<EntityType<?>> values;

    public SimpleSoulIngredient(HolderSet<EntityType<?>> values) {
        if (values.isImmediatelyResolvable()) {
            values.unwrap().ifRight(list -> {
                if (list.isEmpty()) {
                    throw new UnsupportedOperationException("Fluid ingredients can't be empty!");
                }
            });
        }

        this.values = values;
    }

    @Override
    public boolean test(Soul soul) {
        return values.contains(soul.entityType().builtInRegistryHolder());
    }

    @Override
    protected Stream<Holder<EntityType<?>>> generateEntityTypes() {
        return values.stream();
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public SoulIngredientType<?> getType() {
        return TYPE;
    }

    @Override
    public int hashCode() {
        return entityTypeSet().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof SimpleSoulIngredient other && other.entityTypeSet().equals(this.entityTypeSet());
    }

    public HolderSet<EntityType<?>> entityTypeSet() {
        return values;
    }
}
