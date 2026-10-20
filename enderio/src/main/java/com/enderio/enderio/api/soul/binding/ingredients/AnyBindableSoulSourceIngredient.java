package com.enderio.enderio.api.soul.binding.ingredients;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.binding.BindableSoulSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

public class AnyBindableSoulSourceIngredient implements ICustomIngredient {
    public static final AnyBindableSoulSourceIngredient INSTANCE = new AnyBindableSoulSourceIngredient();

    public static final MapCodec<AnyBindableSoulSourceIngredient> CODEC = MapCodec.unit(INSTANCE);

    public static final IngredientType<AnyBindableSoulSourceIngredient> TYPE = new IngredientType<>(CODEC);

    @Nullable
    private Item[] items;

    private AnyBindableSoulSourceIngredient() {
    }

    @Override
    public boolean test(ItemStack stack) {
        BindableSoulSource bindableSoulSource = ItemAccess.forStack(stack).getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE);
        return bindableSoulSource != null && bindableSoulSource.isSoulAvailable();
    }

    @Override
    public Stream<Holder<Item>> items() {
        ensureComputed();
        return Stream.of(items).map(Item::builtInRegistryHolder);
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
            .filter(item -> item != Items.AIR)
            .filter(item -> ItemAccess.forStack(item.getDefaultInstance()).getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE) != null)
            .toArray(Item[]::new);
    }
}
