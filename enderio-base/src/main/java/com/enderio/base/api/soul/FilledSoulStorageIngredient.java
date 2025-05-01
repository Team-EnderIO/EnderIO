package com.enderio.base.api.soul;

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

public class FilledSoulStorageIngredient implements ICustomIngredient {

    public static final MapCodec<FilledSoulStorageIngredient> CODEC = RecordCodecBuilder
            .mapCodec(inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item))
                    .apply(inst, FilledSoulStorageIngredient::new));

    private final Item item;

    public static Ingredient of(ItemLike item) {
        return new FilledSoulStorageIngredient(item.asItem()).toVanilla();
    }

    public FilledSoulStorageIngredient(Item item) {
        this.item = item;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (!itemStack.is(item)) {
            return false;
        }

        var soulStorage = itemStack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        return soulStorage != null && soulStorage.hasSoul();
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(item.getDefaultInstance());
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
