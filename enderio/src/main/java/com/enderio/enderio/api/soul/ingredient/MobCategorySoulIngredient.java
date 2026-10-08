package com.enderio.enderio.api.soul.ingredient;

import com.enderio.enderio.api.soul.Soul;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Stream;

@ApiStatus.Experimental
@ApiStatus.AvailableSince("10.0.0")
public class MobCategorySoulIngredient extends SoulIngredient {

    private static final MapCodec<MobCategorySoulIngredient> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(MobCategory.CODEC.fieldOf("category").forGetter(MobCategorySoulIngredient::category))
            .apply(inst, MobCategorySoulIngredient::new));

    private static final StreamCodec<ByteBuf, MobCategory> CATEGORY_STREAM_CODEC = ByteBufCodecs.STRING_UTF8
        .map(name -> ((StringRepresentable.EnumCodec<MobCategory>) MobCategory.CODEC).byName(name), MobCategory::getName);

    private static final StreamCodec<RegistryFriendlyByteBuf, MobCategorySoulIngredient> STREAM_CODEC = CATEGORY_STREAM_CODEC
        .map(MobCategorySoulIngredient::new, MobCategorySoulIngredient::category).cast();

    public static final SoulIngredientType<MobCategorySoulIngredient> TYPE = new SoulIngredientType<>(CODEC, STREAM_CODEC);

    private final MobCategory category;

    public MobCategorySoulIngredient(MobCategory category) {
        this.category = category;
    }

    public MobCategory category() {
        return category;
    }

    @Override
    public boolean test(Soul soul) {
        return soul.entityType() != null && soul.entityType().getCategory() == category;
    }

    @Override
    protected Stream<Holder<EntityType<?>>> generateEntityTypes() {
        return BuiltInRegistries.ENTITY_TYPE.stream()
            .filter(entityType -> entityType.getCategory() == category)
            .map(EntityType::builtInRegistryHolder);
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
        return category.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof MobCategorySoulIngredient other && other.category() == category();
    }
}
