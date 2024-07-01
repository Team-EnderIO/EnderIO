package com.enderio.conduits.common.recipe;

import com.enderio.api.conduit.ConduitType;
import com.enderio.conduits.common.components.RepresentedConduitType;
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
                ConduitType.CODEC.fieldOf("conduit_type").forGetter(ConduitIngredient::conduitType)
            ).apply(builder, ConduitIngredient::new)
    );

    private final Holder<ConduitType<?, ?, ?>> conduitType;

    private ConduitIngredient(Holder<ConduitType<?, ?, ?>> conduitType) {
        this.conduitType = conduitType;
    }

    public static Ingredient of(Holder<ConduitType<?, ?, ?>> conduitType) {
        return new ConduitIngredient(conduitType).toVanilla();
    }

    public Holder<ConduitType<?, ?, ?>> conduitType() {
        return conduitType;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!stack.is(ConduitBlocks.CONDUIT.asItem())) {
            return false;
        }

        RepresentedConduitType representedConduitType = stack.get(ConduitComponents.REPRESENTED_CONDUIT_TYPE);
        if (representedConduitType == null) {
            return false;
        }

        return conduitType.value().equals(representedConduitType.conduitType());
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
