package com.enderio.enderio.foundation.recipe;

import com.enderio.enderio.api.soul.binding.ingredients.AnySoulBindableIngredient;
import com.enderio.enderio.content.paint.block.PaintedBlock;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

public class PaintIngredient implements ICustomIngredient {
    private static final PaintIngredient INSTANCE = new PaintIngredient();
    public static final MapCodec<PaintIngredient> CODEC = MapCodec.unit(INSTANCE);
    public static final IngredientType<PaintIngredient> TYPE = new IngredientType<>(CODEC);

    public static Ingredient of() {
        return INSTANCE.toVanilla();
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof PaintedBlock) {
                return false;
            }

            return block.defaultBlockState().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        }
        return false;
    }

    @Override
    public Stream<Holder<Item>> items() {
        return BuiltInRegistries.BLOCK.stream().map(b -> b.asItem().builtInRegistryHolder());
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
