package com.enderio.enderio.content.machines.vat;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.datamap.VatReagent;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class FermentingRecipe implements MachineRecipe<FermentingRecipe.Input> {

    private static final StreamCodec<ByteBuf, TagKey<Item>> ITEM_TAG_STREAM_CODEC = Identifier.STREAM_CODEC.map(loc -> TagKey.create(Registries.ITEM, loc),
        TagKey::location);

    public static final MapCodec<FermentingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance
            .group(SizedFluidIngredient.CODEC.fieldOf("input").forGetter(FermentingRecipe::input), //TODO make sure this handles empty
                TagKey.codec(Registries.ITEM).fieldOf("first_reagent").forGetter(FermentingRecipe::firstReagent),
                TagKey.codec(Registries.ITEM).fieldOf("second_reagent").forGetter(FermentingRecipe::secondReagent),
                FluidStack.CODEC.fieldOf("output").forGetter(FermentingRecipe::output), Codec.INT.fieldOf("ticks").forGetter(FermentingRecipe::ticks))
            .apply(instance, FermentingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> STREAM_CODEC = StreamCodec.composite(SizedFluidIngredient.STREAM_CODEC,
        FermentingRecipe::input, ITEM_TAG_STREAM_CODEC, FermentingRecipe::firstReagent, ITEM_TAG_STREAM_CODEC, FermentingRecipe::secondReagent, FluidStack.STREAM_CODEC, FermentingRecipe::output, ByteBufCodecs.INT, FermentingRecipe::ticks,
        FermentingRecipe::new);

    public static final RecipeSerializer<FermentingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final SizedFluidIngredient input;
    private final TagKey<Item> firstReagent;
    private final TagKey<Item> secondReagent;
    private final FluidStack output;
    private final int ticks;

    @Nullable
    private PlacementInfo placementInfo;

    public FermentingRecipe(SizedFluidIngredient input, TagKey<Item> firstReagent, TagKey<Item> secondReagent, FluidStack output, int ticks) {
        this.input = input;
        this.firstReagent = firstReagent;
        this.secondReagent = secondReagent;
        this.output = output;
        this.ticks = ticks;
    }

    @Override
    public int getBaseEnergyCost() {
        return 0;
    }

    @Override
    public List<OutputStack> craft(Input input, RegistryAccess registryAccess) {
        ItemStack firstInput = input.getItem(0);
        ItemStack secondInput = input.getItem(1);

        // Build modifier, ensure we use the correct item for each reagent
        double modifier;
        if (firstInput.is(firstReagent) && secondInput.is(secondReagent)) {
            modifier = getModifier(firstInput, firstReagent);
            modifier *= getModifier(secondInput, secondReagent);
        } else {
            modifier = getModifier(secondInput, firstReagent);
            modifier *= getModifier(firstInput, secondReagent);
        }

        return List.of(OutputStack.of(new FluidStack(output.getFluid(), (int) (output.getAmount() * modifier))));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output().copy()));
    }

    @Override
    public boolean matches(Input input, Level level) {
        FluidStack inputFluid = input.getInputFluid();
        if (!this.input.test(inputFluid) || inputFluid.getAmount() < this.input.amount()) {
            return false;
        }

        ItemStack firstInput = input.getItem(0);
        ItemStack secondInput = input.getItem(1);

        // Order independent check
        return (firstInput.is(firstReagent) && secondInput.is(secondReagent)) ||
            (firstInput.is(secondReagent) && secondInput.is(firstReagent));
    }

    public static double getModifier(ItemStack stack, TagKey<Item> reagent) {
        var map = stack.typeHolder().getData(VatReagent.DATA_MAP);
        if (map != null) {
            return map.getOrDefault(reagent, 1D);
        }
        return 1;
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipeTypes.VAT_FERMENTING.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.FERMENTING.get();
    }

    public SizedFluidIngredient input() {
        return input;
    }

    public TagKey<Item> firstReagent() {
        return firstReagent;
    }

    public TagKey<Item> secondReagent() {
        return secondReagent;
    }

    public FluidStack output() {
        return output;
    }

    public int ticks() {
        return ticks;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) { //TODO not great
            var leftIngredient = Ingredient.of(BuiltInRegistries.ITEM.get(firstReagent).orElseThrow());
            var rightIngredient = Ingredient.of(BuiltInRegistries.ITEM.get(secondReagent).orElseThrow());
            placementInfo = PlacementInfo.create(List.of(leftIngredient, rightIngredient));
        }
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new FermentingDisplay(
            input.ingredient().display(),
            new SlotDisplay.TagSlotDisplay(firstReagent),
            new SlotDisplay.TagSlotDisplay(secondReagent),
            new FluidStackSlotDisplay(output),
            new SlotDisplay.ItemSlotDisplay(EIOBlocks.VAT.asItem())
            ));
    }

    public record Input(ItemStack firstReagent, ItemStack secondStack, FluidStack inputFluid) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
                case 0 -> firstReagent;
                case 1 -> secondStack;
                default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public int size() {
            return 2;
        }

        public FluidStack getInputFluid() {
            return inputFluid;
        }
    }

    public record FermentingDisplay(SlotDisplay fluid, SlotDisplay first, SlotDisplay second, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        public static final MapCodec<FermentingDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_379634_ -> p_379634_.group(
                    SlotDisplay.CODEC.fieldOf("ingredients").forGetter(FermentingDisplay::fluid),
                    SlotDisplay.CODEC.fieldOf("first").forGetter(FermentingDisplay::first),
                    SlotDisplay.CODEC.fieldOf("second").forGetter(FermentingDisplay::second),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(FermentingDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FermentingDisplay::craftingStation)
                )
                .apply(p_379634_, FermentingDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            FermentingDisplay::fluid,
            SlotDisplay.STREAM_CODEC,
            FermentingDisplay::first,
            SlotDisplay.STREAM_CODEC,
            FermentingDisplay::second,
            SlotDisplay.STREAM_CODEC,
            FermentingDisplay::result,
            SlotDisplay.STREAM_CODEC,
            FermentingDisplay::craftingStation,
            FermentingDisplay::new
        );
        public static final RecipeDisplay.Type<FermentingDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flagSet) {
            if (!fluid().isEnabled(flagSet)) {
                return false;
            }
            if (!first.isEnabled(flagSet)) {
                return false;
            }
            if (!second().isEnabled(flagSet)) {
                return false;
            }
            if (!result().isEnabled(flagSet)) {
                return false;
            }
            return RecipeDisplay.super.isEnabled(flagSet);
        }
    }
}
