package com.enderio.enderio.api.soul.ingredient;

import com.enderio.enderio.api.soul.Soul;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Stream;

@ApiStatus.Experimental
@ApiStatus.AvailableSince("10.0.0")
public class AnySoulIngredient extends SoulIngredient {

    public static final AnySoulIngredient INSTANCE = new AnySoulIngredient();

    private static final MapCodec<AnySoulIngredient> CODEC = MapCodec.unit(INSTANCE);

    private static final StreamCodec<RegistryFriendlyByteBuf, AnySoulIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static final SoulIngredientType<AnySoulIngredient> TYPE = new SoulIngredientType<>(CODEC, STREAM_CODEC);

    private AnySoulIngredient() {
    }

    @Override
    public boolean test(Soul soul) {
        return true;
    }

    @Override
    protected Stream<Holder<EntityType<?>>> generateEntityTypes() {
        return BuiltInRegistries.ENTITY_TYPE.stream().map(EntityType::builtInRegistryHolder);
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
        return 0xDEADBEEF;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == INSTANCE;
    }
}
