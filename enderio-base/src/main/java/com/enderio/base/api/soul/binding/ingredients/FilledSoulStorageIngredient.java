package com.enderio.base.api.soul.binding.ingredients;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.SoulBoundUtils;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOIngredientTypes;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public class FilledSoulStorageIngredient implements ICustomIngredient {

    public static final MapCodec<FilledSoulStorageIngredient> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item)).apply(inst, FilledSoulStorageIngredient::new));

    private final Item item;

    public static Ingredient of(ItemLike item) {
        return new FilledSoulStorageIngredient(item.asItem()).toVanilla();
    }

    public FilledSoulStorageIngredient(Item item) {
        this.item = item;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.is(item) && SoulBoundUtils.isBound(itemStack);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return EntityCaptureUtils.getCapturableEntityTypes().stream().map(entityType -> {
            var stack = item.getDefaultInstance();
            if (SoulBoundUtils.tryBindSoul(stack, Soul.of(entityType))) {
                return Optional.of(stack);
            }

            return Optional.<ItemStack>empty();
        }).flatMap(Optional::stream);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return EIOIngredientTypes.FILLED_SOUL_STORAGE.get();
    }
}
