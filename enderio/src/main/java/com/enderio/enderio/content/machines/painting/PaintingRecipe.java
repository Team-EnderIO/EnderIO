package com.enderio.enderio.content.machines.painting;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.paint.BlockPaintData;
import com.enderio.enderio.content.paint.block.PaintedBlock;
import com.enderio.enderio.content.paint.item.PaintedBlockItem;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.recipe.PaintIngredient;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipeTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class PaintingRecipe implements MachineRecipe<PaintingRecipe.Input> {
    public static final MapCodec<PaintingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(Ingredient.CODEC.fieldOf("input").forGetter(PaintingRecipe::input), ItemStackTemplate.CODEC.fieldOf("output").forGetter(PaintingRecipe::output))
        .apply(instance, PaintingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PaintingRecipe> STREAM_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC,
        PaintingRecipe::input, ItemStackTemplate.STREAM_CODEC, PaintingRecipe::output, PaintingRecipe::new);

    public static final RecipeSerializer<PaintingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Ingredient input;
    private final ItemStackTemplate output;

    @Nullable
    private PlacementInfo placementInfo;

    public PaintingRecipe(Ingredient input, ItemStackTemplate output) {
        this.input = input;
        this.output = output;
    }

    public Ingredient input() {
        return input;
    }

    public ItemStackTemplate output() {
        return output;
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        return input.test(recipeInput.getItem(0)) && !recipeInput.getItem(1).isEmpty();
    }

    @Override
    public int getBaseEnergyCost() {
        return MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_ENERGY_COST.get();
    }

    @Override
    public List<OutputStack> craft(Input recipeInput, RegistryAccess registryAccess) {
        List<OutputStack> outputs = new ArrayList<>();
        ItemStack outputStack = output.create();

        var paintItem = recipeInput.getItem(1);
        if (!(paintItem.getItem() instanceof BlockItem blockItem)) {
            throw new IllegalStateException("The item must be a block item.");
        }

        var paintBlock = blockItem.getBlock();
        outputStack.set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(paintBlock));

        outputs.add(OutputStack.of(outputStack));
        return outputs;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.create()));
    }

    @Override
    public ItemStack assemble(Input recipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public List<RecipeDisplay> display() {
        var blocks = PaintIngredient.of().items().filter(s -> PaintIngredient.of().test(new ItemStack(s))).map(s ->
            ((BlockItem) s.value()).getBlock()).toList();

        List<SlotDisplay> results = new ArrayList<>();
        for (Block block : blocks) {
            results.add(new SlotDisplay.ItemStackSlotDisplay(
                new ItemStackTemplate(output.item(),
                    DataComponentPatch.builder().set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(block)).build())));
        }

        return List.of(new PaintingDisplay(input.display(), new SlotDisplay.Composite(results),
            new SlotDisplay.ItemSlotDisplay(EIOBlocks.PAINTING_MACHINE.asItem())));
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipeTypes.PAINTING.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.PAINTING.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(List.of(input, PaintIngredient.of()));
        }

        return placementInfo;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    public record Input(ItemStack template, ItemStack paint) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
                case 0 -> template;
                case 1 -> paint;
                default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public int size() {
            return 2;
        }
    }

    public record PaintingDisplay(SlotDisplay ingredient, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        public static final MapCodec<PaintingDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(p_379634_ -> p_379634_
            .group(SlotDisplay.CODEC.fieldOf("ingredients").forGetter(PaintingDisplay::ingredient),
                SlotDisplay.CODEC.fieldOf("result").forGetter(PaintingDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(PaintingDisplay::craftingStation))
            .apply(p_379634_, PaintingDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, PaintingDisplay> STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC,
            PaintingDisplay::ingredient, SlotDisplay.STREAM_CODEC, PaintingDisplay::result, SlotDisplay.STREAM_CODEC, PaintingDisplay::craftingStation,
            PaintingDisplay::new);
        public static final Type<PaintingDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flagSet) {
            return this.ingredient.isEnabled(flagSet) && RecipeDisplay.super.isEnabled(flagSet);
        }
    }
}
