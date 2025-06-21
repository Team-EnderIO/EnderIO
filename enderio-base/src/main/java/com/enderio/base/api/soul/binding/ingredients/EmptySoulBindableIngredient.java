package com.enderio.base.api.soul.binding.ingredients;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.SoulBoundUtils;
import com.enderio.base.api.soul.storage.ISoulHandler;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOIngredientTypes;
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

import java.util.stream.Stream;

public class EmptySoulBindableIngredient implements ICustomIngredient {

    public static final MapCodec<EmptySoulBindableIngredient> CODEC = RecordCodecBuilder
            .mapCodec(inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item))
                    .apply(inst, EmptySoulBindableIngredient::new));

    private final Item item;
    private final ItemStack itemStack;

    public static Ingredient of(ItemLike item) {
        return new EmptySoulBindableIngredient(item.asItem()).toVanilla();
    }

    public EmptySoulBindableIngredient(Item item) {
        this.item = item;

        // Attempt to get the unbound item.
        var stack = item.getDefaultInstance();
        if (!SoulBoundUtils.canBindSoul(stack)) {
            ISoulHandler soulHandler = stack.getCapability(EIOCapabilities.SoulHandler.ITEM);
            if (soulHandler != null && soulHandler.tryInsertSoul(Soul.EMPTY, true)) { //Can't bind a soul, but can store one
                itemStack = stack;
                return;
            }
            itemStack = new ItemStack(Blocks.BARRIER);
            itemStack.set(DataComponents.CUSTOM_NAME, Component.literal("Item cannot be bound: " + stack.getHoverName()));
        } else if (SoulBoundUtils.tryBindSoul(stack, Soul.EMPTY)) {
            itemStack = stack;
        } else {
            ItemStack errorStack = new ItemStack(Blocks.BARRIER);
            errorStack.set(DataComponents.CUSTOM_NAME, Component.literal("Unable to empty binding of " + stack.getHoverName()));
            itemStack = errorStack;
        }
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.is(item) && !SoulBoundUtils.isBound(itemStack);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(itemStack);
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
