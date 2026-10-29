package com.enderio.enderio.foundation.ingredients;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class AnySpawnEggIngredient implements ICustomIngredient {
    public static final AnySpawnEggIngredient INSTANCE = new AnySpawnEggIngredient();

    public static final MapCodec<AnySpawnEggIngredient> CODEC = MapCodec.unit(INSTANCE);

    public static final IngredientType<AnySpawnEggIngredient> TYPE = new IngredientType<>(CODEC);

    @Nullable
    private List<Item> items;

    @Override
    public boolean test(ItemStack stack) {
        ensureComputed();
        return stack.is(itemHolder -> items.contains(itemHolder.value()));
    }

    @Override
    public Stream<Holder<Item>> items() {
        ensureComputed();
        return items.stream().map(Item::builtInRegistryHolder);
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public IngredientType<?> getType() {
        return TYPE;
    }

    private void ensureComputed() {
        if (items != null) {
            return;
        }

        // List of any bindable soul sources in the game.
        items = BuiltInRegistries.ITEM.stream()
            .filter(item -> item instanceof SpawnEggItem)
            .toList();
    }
}
