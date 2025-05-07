package com.enderio.base.api.soul.binding.ingredients;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.SoulBoundUtils;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOIngredientTypes;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

public class FilledSoulStorageIngredient implements ICustomIngredient {

    public static final MapCodec<FilledSoulStorageIngredient> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item)).apply(inst, FilledSoulStorageIngredient::new));

    private final Item item;
    private final ItemStack[] itemStacks;

    public static Ingredient of(ItemLike item) {
        return new FilledSoulStorageIngredient(item.asItem()).toVanilla();
    }

    public FilledSoulStorageIngredient(Item item) {
        this.item = item;

        // Pre-compute
        var defaultStack = item.getDefaultInstance();
        if (!SoulBoundUtils.canBindSoul(defaultStack)) {
            var errorStack = new ItemStack(Blocks.BARRIER);
            errorStack.set(DataComponents.CUSTOM_NAME, Component.literal("Item cannot be bound: " + defaultStack.getHoverName()));
            itemStacks = new ItemStack[] {errorStack};
        } else {
            itemStacks = EntityCaptureUtils.getCapturableEntityTypes().stream().map(entityType -> {
                var stack = item.getDefaultInstance();
                if (SoulBoundUtils.tryBindSoul(stack, Soul.of(entityType))) {
                    return Optional.of(stack);
                }

                return Optional.<ItemStack>empty();
            }).flatMap(Optional::stream).toArray(ItemStack[]::new);
        }
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.is(item) && SoulBoundUtils.isBound(itemStack);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(itemStacks);
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
