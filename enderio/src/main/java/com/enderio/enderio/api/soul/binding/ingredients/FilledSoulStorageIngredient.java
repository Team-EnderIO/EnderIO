package com.enderio.enderio.api.soul.binding.ingredients;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.foundation.util.EntityCaptureUtils;
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

// TODO: 26.1 - move to the right package.
public class FilledSoulStorageIngredient implements ICustomIngredient {

    public static final MapCodec<FilledSoulStorageIngredient> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item)).apply(inst, FilledSoulStorageIngredient::new));

    public static final IngredientType<FilledSoulStorageIngredient> TYPE = new IngredientType<>(CODEC);

    private final Item item;
    private final ItemStack[] itemStacks;

    public static Ingredient of(ItemLike item) {
        return new FilledSoulStorageIngredient(item.asItem()).toVanilla();
    }

    public FilledSoulStorageIngredient(Item item) {
        this.item = item;

        // Pre-compute all valid stacks
        var defaultStack = item.getDefaultInstance();
        if (defaultStack.getCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM) == null) {
            var errorStack = new ItemStack(Blocks.BARRIER);
            errorStack.set(DataComponents.CUSTOM_NAME, Component.literal("Item cannot store souls: " + defaultStack.getHoverName()));
            itemStacks = new ItemStack[] {errorStack};
        } else {
            itemStacks = EntityCaptureUtils.getCapturableEntityTypes().stream().map(entityType -> {
                var stack = item.getDefaultInstance();
                var soulHandler = stack.getCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM);
                if (soulHandler != null && soulHandler.tryInsertSoul(Soul.of(entityType), false)) {
                    return Optional.of(stack);
                }

                return Optional.<ItemStack>empty();
            }).flatMap(Optional::stream).toArray(ItemStack[]::new);
        }
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (!itemStack.is(item)) {
            return false;
        }

        var soulHandler = itemStack.getCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM);
        if (soulHandler == null) {
            return false;
        }

        for (int slot = 0; slot < soulHandler.getSlots(); slot++) {
            if (!soulHandler.getSoulInSlot(slot).isEmpty()) {
                return true;
            }
        }

        return false;
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
        return TYPE;
    }
}
