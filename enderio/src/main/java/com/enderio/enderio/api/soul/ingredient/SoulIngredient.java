package com.enderio.enderio.api.soul.ingredient;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.soul.Soul;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@ApiStatus.Experimental
@ApiStatus.AvailableSince("10.0.0")
public abstract class SoulIngredient implements Predicate<Soul> {

    public static final Codec<SoulIngredient> CODEC = Codec.xor(
            EnderIORegistries.SOUL_INGREDIENT_TYPES.byNameCodec().<SoulIngredient>dispatch("type", SoulIngredient::getType, SoulIngredientType::codec),
            // Avoid class referencing issues :)
            Codec.lazyInitialized(() -> SimpleSoulIngredient.CONTENTS_CODEC))
        .xmap(either -> either.map(i -> i, i -> i), ingredient -> switch (ingredient) {
            case SimpleSoulIngredient simple -> Either.right(simple);
            default -> Either.left(ingredient);
        });

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulIngredient> STREAM_CODEC = ByteBufCodecs.registry(EnderIORegistries.Keys.SOUL_INGREDIENT_TYPES)
        .dispatch(SoulIngredient::getType, SoulIngredientType::streamCodec);

    @Nullable
    private List<Holder<EntityType<?>>> entityTypes;

    public final List<Holder<EntityType<?>>> getEntityTypes() {
        if (entityTypes == null) {
            entityTypes = generateEntityTypes().toList();
        }

        return entityTypes;
    }

    @Override
    public abstract boolean test(Soul soul);

    protected abstract Stream<Holder<EntityType<?>>> generateEntityTypes();

    // TODO: Consider SlotDisplay

    public abstract boolean isSimple();

    public abstract SoulIngredientType<?> getType();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public static SoulIngredient any() {
        return AnySoulIngredient.INSTANCE;
    }

    public static SoulIngredient of(Soul... souls) {
        for (var soul : souls) {
            if (soul.isEmpty()) {
                throw new IllegalArgumentException("Cannot create SoulIngredient with any empty Souls");
            }
        }

        //noinspection NullableProblems
        return of(Arrays.stream(souls).map(Soul::entityType));
    }

    public static SoulIngredient of(EntityType<?>... entityTypes) {
        return of(Arrays.stream(entityTypes));
    }

    public static SoulIngredient of(Stream<EntityType<?>> entityTypes) {
        return of(HolderSet.direct(entityTypes.map(EntityType::builtInRegistryHolder).toList()));
    }

    public static SoulIngredient of(HolderSet<EntityType<?>> values) {
        return new SimpleSoulIngredient(values);
    }

    public static SoulIngredient of(MobCategory category) {
        return new MobCategorySoulIngredient(category);
    }
}
