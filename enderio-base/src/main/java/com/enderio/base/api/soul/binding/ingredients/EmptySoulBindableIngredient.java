package com.enderio.base.api.soul.binding.ingredients;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.SoulBoundUtils;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOIngredientTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public class EmptySoulBindableIngredient implements ICustomIngredient {

    public static final MapCodec<EmptySoulBindableIngredient> CODEC = RecordCodecBuilder
            .mapCodec(inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item))
                    .apply(inst, EmptySoulBindableIngredient::new));

    private final Item item;

    public static Ingredient of(ItemLike item) {
        return new EmptySoulBindableIngredient(item.asItem()).toVanilla();
    }

    public EmptySoulBindableIngredient(Item item) {
        this.item = item;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.is(item) && !SoulBoundUtils.isBound(itemStack);
    }

    @Override
    public Stream<ItemStack> getItems() {
        var stack = item.getDefaultInstance();
        var soulBindable = stack.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable != null) {
            // If we can't bind empty, don't include it.
            if (!SoulBoundUtils.tryBindSoul(soulBindable, Soul.EMPTY) && soulBindable.hasSoul()) {
                return Stream.of();
            }
        }

        // Assume no capability == empty...
        return Stream.of(stack);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return EIOIngredientTypes.EMPTY_SOUL_STORAGE.get();
    }
}
