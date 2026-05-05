package com.enderio.enderio.content.machines.alloy;

import com.enderio.core.common.crafting.WithCountSlotDisplay;
import com.enderio.enderio.api.recipes.EnderIORecipe;
import com.enderio.enderio.api.recipes.alloy.AlloySmeltingInput;
import com.enderio.enderio.api.recipes.alloy.AlloySmeltingRecipeDisplay;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

public final class AlloySmeltingRecipe implements EnderIORecipe<AlloySmeltingInput> {
    // Uses optional field for isSmelting to avoid polluting recipe generation.
    // TODO: Validation to ensure inputs has a size of 1 if isSmelting is true.
    public static final MapCodec<AlloySmeltingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(SizedIngredient.NESTED_CODEC.listOf(1, 3).fieldOf("inputs").forGetter(AlloySmeltingRecipe::inputs), //TODO is nested right?
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(AlloySmeltingRecipe::output),
            Codec.INT.fieldOf("operation_time").forGetter(AlloySmeltingRecipe::baseOperationTime),
            Codec.FLOAT.fieldOf("experience").forGetter(AlloySmeltingRecipe::experience),
            Codec.BOOL.optionalFieldOf("is_smelting", false).forGetter(AlloySmeltingRecipe::isSmelting))
        .apply(inst, AlloySmeltingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> STREAM_CODEC = StreamCodec
        .composite(SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), AlloySmeltingRecipe::inputs,
            ItemStackTemplate.STREAM_CODEC, AlloySmeltingRecipe::output, ByteBufCodecs.INT,
            AlloySmeltingRecipe::baseOperationTime, ByteBufCodecs.FLOAT, AlloySmeltingRecipe::experience,
            ByteBufCodecs.BOOL, AlloySmeltingRecipe::isSmelting, AlloySmeltingRecipe::new);

    public static final RecipeSerializer<AlloySmeltingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final List<SizedIngredient> inputs;
    private final ItemStackTemplate output;
    private final int operationTime;
    private final float experience;
    private final boolean isSmelting;
    @Nullable
    private PlacementInfo placementInfo;

    public AlloySmeltingRecipe(List<SizedIngredient> inputs, ItemStackTemplate output, int operationTime, float experience,
            boolean isSmelting) {
        if (isSmelting && inputs.size() > 1) {
            throw new IllegalArgumentException("More than one smelting ingredient given");
        }

        this.inputs = inputs;
        this.output = output;
        this.operationTime = operationTime;
        this.experience = experience;
        this.isSmelting = isSmelting;
    }

    public AlloySmeltingRecipe(List<SizedIngredient> inputs, ItemStackTemplate output, int operationTime, float experience) {
        this(inputs, output, operationTime, experience, false);
    }

    public List<SizedIngredient> inputs() {
        return inputs;
    }

    public ItemStackTemplate output() {
        return output;
    }

    public int baseOperationTime() {
        return operationTime;
    }

    public int getOperationTime(AlloySmeltingInput input) {
        if (isSmelting) {
            return operationTime * getSmeltingInputCount(input);
        }

        return operationTime;
    }

    public float experience() {
        return experience;
    }

    public boolean isSmelting() {
        return isSmelting;
    }

    public int getSmeltingInputCount(AlloySmeltingInput recipeInput) {
        if (!isSmelting) {
            throw new IllegalStateException("Not a smelting recipe");
        }

        int inputCount = IntStream.range(0, recipeInput.size())
            .filter(i -> !recipeInput.getItem(i).isEmpty())
            .filter(i -> inputs.getFirst().test(recipeInput.getItem(i)))
            .map(i -> recipeInput.getItem(i).getCount())
            .sum();

        return Math.min(inputCount, 3);
    }

    @Override
    public boolean matches(AlloySmeltingInput recipeInput, Level level) {
        if (inputs.isEmpty()) {
            return false;
        }

        if (isSmelting && !recipeInput.mode().canSmelt()) {
            return false;
        } else if (!isSmelting && !recipeInput.mode().canAlloy()) {
            return false;
        }

        // Simpler smelting match logic
        if (isSmelting) {
            // Ensure that all non-empty inputs match the ingredient.
            return IntStream.range(0, recipeInput.size())
                .filter(i -> !recipeInput.getItem(i).isEmpty())
                .allMatch(i -> inputs.getFirst().test(recipeInput.getItem(i)));
        }

        boolean[] matchedInputs = new boolean[3];

        for (int slot = 0; slot < 3; slot++) {
            var slotItem = recipeInput.getItem(slot);

            for (int ingredient = 0; ingredient < 3; ingredient++) {
                // If this ingredient has been matched already, continue
                if (matchedInputs[ingredient]) {
                    continue;
                }

                if (ingredient < inputs.size()) {
                    // If we expect an input, test we have a match for it.
                    if (inputs.get(ingredient).test(slotItem)) {
                        matchedInputs[ingredient] = true;
                        break;
                    }
                } else if (slotItem.isEmpty()) {
                    // If we don't expect an input, make sure we have a blank for it.
                    matchedInputs[ingredient] = true;
                    break;
                }
            }
        }

        // If we matched all our ingredients, we win!
        return matchedInputs[0] && matchedInputs[1] && matchedInputs[2];
    }

    @Override
    public ItemStack assemble(AlloySmeltingInput input) {
        ItemStack outputStack = output.create();
        if (isSmelting) {
            outputStack.setCount(outputStack.getCount() * getSmeltingInputCount(input));
        }

        return outputStack;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
            new AlloySmeltingRecipeDisplay(
                this.inputs.stream().<SlotDisplay>map(WithCountSlotDisplay::new).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.output),
                new SlotDisplay.ItemSlotDisplay(EIOBlocks.ALLOY_SMELTER.asItem()),
                baseOperationTime()
            )
        );
    }

    @Override
    public RecipeSerializer<? extends Recipe<AlloySmeltingInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<AlloySmeltingInput>> getType() {
        return EIORecipeTypes.ALLOY_SMELTING.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(inputs.stream().map(SizedIngredient::ingredient).toList());
        }
        return placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.ALLOY_SMELTING.get();
    }
}
