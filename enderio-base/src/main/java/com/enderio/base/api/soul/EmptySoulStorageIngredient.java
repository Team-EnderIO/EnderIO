package com.enderio.base.api.soul;

import com.enderio.base.api.attachment.StoredEntityData;
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

public class EmptySoulStorageIngredient implements ICustomIngredient {

    public static final MapCodec<EmptySoulStorageIngredient> CODEC = RecordCodecBuilder
            .mapCodec(inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item))
                    .apply(inst, EmptySoulStorageIngredient::new));

    private final Item item;

    public static Ingredient of(ItemLike item) {
        return new EmptySoulStorageIngredient(item.asItem()).toVanilla();
    }

    public EmptySoulStorageIngredient(Item item) {
        this.item = item;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (!itemStack.is(item)) {
            return false;
        }

        var soulStorage = itemStack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        return soulStorage != null && !soulStorage.hasSoul();
    }

    @Override
    public Stream<ItemStack> getItems() {
        var stack = item.getDefaultInstance();
        var soulStorage = stack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        if (soulStorage != null) {
            soulStorage.setSoul(StoredEntityData.EMPTY);
        }

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
