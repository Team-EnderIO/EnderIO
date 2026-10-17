package com.enderio.enderio.api.soul.storage.ingredients;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.api.soul.SoulResource;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
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
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

public class EmptySoulHandlerIngredient implements ICustomIngredient {

    public static final MapCodec<EmptySoulHandlerIngredient> CODEC = RecordCodecBuilder
            .mapCodec(inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(i -> i.item))
                    .apply(inst, EmptySoulHandlerIngredient::new));

    public static final IngredientType<EmptySoulHandlerIngredient> TYPE = new IngredientType<>(CODEC);

    private final Item item;

    @Nullable
    private ItemStack itemStack;

    public static Ingredient of(ItemLike item) {
        return new EmptySoulHandlerIngredient(item.asItem()).toVanilla();
    }

    public EmptySoulHandlerIngredient(Item item) {
        this.item = item;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        ensureComputed();
        return itemStack.is(item) && !SoulBoundUtils.isBound(itemStack);
    }

    @Override
    public Stream<Holder<Item>> items() {
        ensureComputed();
        return Stream.of(itemStack).map(ItemStack::typeHolder);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return TYPE;
    }

    private void ensureComputed() {
        if (itemStack != null) {
            return;
        }

        // Attempt to get the unbound item.
        var stack = item.getDefaultInstance();
        ResourceHandler<SoulResource> soulHandler = ItemAccess.forStack(stack).getCapability(EnderIOCapabilities.Soul.ITEM);
        if (soulHandler != null) {
            itemStack = stack;
        } else {
            ItemStack errorStack = new ItemStack(Blocks.BARRIER);
            errorStack.set(DataComponents.CUSTOM_NAME, Component.literal("Unable to store souls in " + stack.getHoverName()));
            itemStack = errorStack;
        }
    }
}
