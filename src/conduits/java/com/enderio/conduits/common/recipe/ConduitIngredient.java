package com.enderio.conduits.common.recipe;

import com.enderio.api.conduit.Conduit;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.conduits.common.init.ConduitIngredientTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

public class ConduitIngredient implements ICustomIngredient {

    public static final MapCodec<ConduitIngredient> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder
            .group(
                Conduit.CODEC.fieldOf("conduit_type").forGetter(ConduitIngredient::conduitType)
            ).apply(builder, ConduitIngredient::new)
    );

    private final Holder<Conduit<?, ?, ?>> conduitType;

    private ConduitIngredient(Holder<Conduit<?, ?, ?>> conduitType) {
        this.conduitType = conduitType;
    }

    public static Ingredient of(Holder<Conduit<?, ?, ?>> conduitType) {
        return new ConduitIngredient(conduitType).toVanilla();
    }

    public Holder<Conduit<?, ?, ?>> conduitType() {
        return conduitType;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!stack.is(ConduitBlocks.CONDUIT.asItem())) {
            return false;
        }

        if (!stack.has(ConduitComponents.CONDUIT)) {
            return false;
        }

        Holder<Conduit<?, ?, ?>> conduit = stack.get(ConduitComponents.CONDUIT);
        return conduitType.value().equals(conduit);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(ConduitBlockItem.getStackFor(conduitType, 1));
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return ConduitIngredientTypes.CONDUIT_INGREDIENT_TYPE.get();
    }
}
