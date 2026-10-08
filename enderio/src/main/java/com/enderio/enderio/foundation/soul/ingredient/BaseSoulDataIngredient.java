package com.enderio.enderio.foundation.soul.ingredient;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.ingredient.SoulIngredient;
import com.enderio.enderio.api.soul.ingredient.SoulIngredientType;
import com.enderio.enderio.init.EIODataMaps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.stream.Stream;

public abstract class BaseSoulDataIngredient extends SoulIngredient {
    private final DataMapType<EntityType<?>, ?> dataMapType;

    protected BaseSoulDataIngredient(DataMapType<EntityType<?>, ?> dataMapType) {
        this.dataMapType = dataMapType;
    }

    @Override
    public boolean test(Soul soul) {
        return soul.entityType() != null && soul.entityType().builtInRegistryHolder().getData(dataMapType) != null;
    }

    @Override
    protected Stream<Holder<EntityType<?>>> generateEntityTypes() {
        return BuiltInRegistries.ENTITY_TYPE.stream()
            .map(EntityType::builtInRegistryHolder)
            .filter(holder -> holder.getData(dataMapType) != null)
            // Silly Java hijinks
            .map(h -> h);
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public int hashCode() {
        return dataMapType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}
