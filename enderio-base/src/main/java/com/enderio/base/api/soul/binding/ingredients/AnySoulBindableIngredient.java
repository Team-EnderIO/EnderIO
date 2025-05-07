package com.enderio.base.api.soul.binding.ingredients;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.SoulBoundUtils;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOIngredientTypes;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Optional;
import java.util.stream.Stream;

public class AnySoulBindableIngredient implements ICustomIngredient {

    public static final MapCodec<AnySoulBindableIngredient> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item)).apply(inst, AnySoulBindableIngredient::new));

    private final Item item;

    public static Ingredient of(ItemLike item) {
        return new AnySoulBindableIngredient(item.asItem()).toVanilla();
    }

    public AnySoulBindableIngredient(Item item) {
        this.item = item;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.is(item);
    }

    @Override
    public Stream<ItemStack> getItems() {
        Stream<Optional<ItemStack>> possibleItems = EntityCaptureUtils.getCapturableEntities().stream().map(entity -> {
            var stack = item.getDefaultInstance();
            if (SoulBoundUtils.tryBindSoul(stack, Soul.of(entity))) {
                return Optional.of(stack);
            }

            return Optional.empty();
        });

        return Stream.concat(Stream.of(item.getDefaultInstance()), possibleItems.flatMap(Optional::stream));
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public IngredientType<?> getType() {
        return EIOIngredientTypes.ANY_SOUL_STORAGE.get();
    }
}
