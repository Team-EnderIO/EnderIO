package com.enderio.enderio.content.machines.alloy;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.Nullable;
import java.util.List;

public class AlloySmeltingRecipe implements MachineRecipe<AlloySmeltingRecipe.Input> {
    // Uses optional field for isSmelting to avoid polluting recipe generation.
    public static final MapCodec<AlloySmeltingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(SizedIngredient.NESTED_CODEC.listOf().fieldOf("inputs").forGetter(AlloySmeltingRecipe::inputs), //TODO is nested right?
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(AlloySmeltingRecipe::output),
            Codec.INT.fieldOf("operation_time").forGetter(AlloySmeltingRecipe::operationTime),
            Codec.FLOAT.fieldOf("experience").forGetter(AlloySmeltingRecipe::experience),
            Codec.BOOL.optionalFieldOf("is_smelting", false).forGetter(AlloySmeltingRecipe::isSmelting))
        .apply(inst, AlloySmeltingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> STREAM_CODEC = StreamCodec
        .composite(SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), AlloySmeltingRecipe::inputs,
            ItemStackTemplate.STREAM_CODEC, AlloySmeltingRecipe::output, ByteBufCodecs.INT,
            AlloySmeltingRecipe::operationTime, ByteBufCodecs.FLOAT, AlloySmeltingRecipe::experience,
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

    public int operationTime() {
        return operationTime;
    }

    public float experience() {
        return experience;
    }

    public boolean isSmelting() {
        return isSmelting;
    }

    @Override
    public int getOperationTime(Input input) {
        return operationTime;
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        if (inputs().isEmpty()) {
            return false;
        }

        if (isSmelting && !recipeInput.mode().canSmelt()) {
            return false;
        } else if (!isSmelting && !recipeInput.mode().canAlloy()) {
            return false;
        }

        // Simpler smelting match logic
        if (isSmelting) {
            int emptyCount = 0;

            for (int i = 0; i < 3; i++) {
                var slotItem = recipeInput.getItem(i);

                if (slotItem.isEmpty()) {
                    emptyCount++;
                    continue;
                }

                if (!inputs().getFirst().test(slotItem)) {
                    return false;
                }
            }

            return emptyCount < 3;
        }

        boolean[] matchedInputs = new boolean[3];

        // Iterate over the slots
        for (int i = 0; i < 3; i++) {
            // Iterate over the inputs
            for (int j = 0; j < 3; j++) {
                // If this ingredient has been matched already, continue
                if (matchedInputs[j]) {
                    continue;
                }

                var slotItem = recipeInput.getItem(i);

                if (j < inputs().size()) {
                    // If we expect an input, test we have a match for it.
                    if (inputs().get(j).test(slotItem)) {
                        matchedInputs[j] = true;
                        break;
                    }
                } else if (slotItem.isEmpty()) {
                    // If we don't expect an input, make sure we have a blank for it.
                    matchedInputs[j] = true;
                    break;
                }
            }
        }

        // If we matched all our ingredients, we win!
        for (int i = 0; i < 3; i++) {
            if (!matchedInputs[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<OutputStack> craft(Input container, RandomSource randomSource, RegistryAccess registryAccess) {
        ItemStack outputStack = output().create();
        if (isSmelting) {
            int inputsConsumed = Math.min(3, container.inputs.stream().mapToInt(ItemStack::getCount).sum());
            outputStack.setCount(outputStack.getCount() * inputsConsumed);
        }
        return List.of(OutputStack.of(outputStack));
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
            new AlloySmelterDisplay(
                this.inputs().stream().map(s -> s.ingredient().display()).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.output()),
                new SlotDisplay.ItemSlotDisplay(EIOBlocks.ALLOY_SMELTER.asItem())
            )
        );
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output().create()));
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipeTypes.ALLOY_SMELTING.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(inputs().stream().map(SizedIngredient::ingredient).toList());
        }
        return placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.ALLOY_SMELTING.get();
    }

    public record Input(AlloySmelterMode mode, List<ItemStack> inputs) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            if (slotIndex >= inputs.size()) {
                throw new IllegalArgumentException("No item for index " + slotIndex);
            }

            return inputs.get(slotIndex);
        }

        @Override
        public int size() {
            return inputs.size();
        }
    }

    public record AlloySmelterDisplay(List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        public static final MapCodec<AlloySmelterDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_379634_ -> p_379634_.group(
                    SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(AlloySmelterDisplay::ingredients),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(AlloySmelterDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(AlloySmelterDisplay::craftingStation)
                )
                .apply(p_379634_, AlloySmelterDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, AlloySmelterDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AlloySmelterDisplay::ingredients,
            SlotDisplay.STREAM_CODEC,
            AlloySmelterDisplay::result,
            SlotDisplay.STREAM_CODEC,
            AlloySmelterDisplay::craftingStation,
            AlloySmelterDisplay::new
        );
        public static final RecipeDisplay.Type<AlloySmelterDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flagSet) {
            return this.ingredients.stream().allMatch(i -> i.isEnabled(flagSet)) && RecipeDisplay.super.isEnabled(flagSet);
        }
    }
}
