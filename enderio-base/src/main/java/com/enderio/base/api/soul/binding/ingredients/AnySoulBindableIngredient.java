package com.enderio.base.api.soul.binding.ingredients;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.SoulBoundUtils;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOIngredientTypes;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import java.util.Optional;
import java.util.stream.Stream;

public class AnySoulBindableIngredient implements ICustomIngredient {

    public static final MapCodec<AnySoulBindableIngredient> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item)).apply(inst, AnySoulBindableIngredient::new));

    private final Item item;
    private final ItemStack[] itemStacks;

    public static Ingredient of(ItemLike item) {
        return new AnySoulBindableIngredient(item.asItem()).toVanilla();
    }

    public AnySoulBindableIngredient(Item item) {
        this.item = item;

        // Pre-compute
        var unboundStack = item.getDefaultInstance();
        var unboundName = unboundStack.getHoverName();

        if (!SoulBoundUtils.canBindSoul(unboundStack)) {
            var errorStack = new ItemStack(Blocks.BARRIER);
            errorStack.set(DataComponents.CUSTOM_NAME, Component.literal("Item cannot be bound: " + unboundStack.getHoverName()));
            itemStacks = new ItemStack[] {unboundStack};
            return;
        }

        // Attempt to get the unbound item.
        if (!SoulBoundUtils.tryBindSoul(unboundStack, Soul.EMPTY)) {
            unboundStack = new ItemStack(Blocks.BARRIER);
            unboundStack.set(DataComponents.CUSTOM_NAME, Component.literal("Unable to empty binding of " + unboundName));
        }

        // Pre-compute all stacks
        itemStacks = Stream.concat(
            Stream.of(unboundStack),
            EntityCaptureUtils.getCapturableEntityTypes().stream().map(entityType -> {
                var stack = item.getDefaultInstance();
                if (SoulBoundUtils.tryBindSoul(stack, Soul.of(entityType))) {
                    return Optional.of(stack);
                }

                return Optional.<ItemStack>empty();
            }).flatMap(Optional::stream)
        ).toArray(ItemStack[]::new);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.is(item);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(itemStacks);
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
