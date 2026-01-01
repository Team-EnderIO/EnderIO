package com.enderio.enderio.content.machines.vat;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.datamap.VatReagent;
import com.enderio.enderio.foundation.io.fluid.MachineFluidTank;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipes;
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

import java.util.List;
import java.util.Objects;

public final class FermentingRecipe implements MachineRecipe<FermentingRecipe.Input> {
    private final SizedFluidIngredient input;
    private final TagKey<Item> leftReagent;
    private final TagKey<Item> rightReagent;
    private final FluidStack output;
    private final int ticks;
    private PlacementInfo placementInfo;

    public FermentingRecipe(SizedFluidIngredient input, TagKey<Item> leftReagent, TagKey<Item> rightReagent, FluidStack output, int ticks) {
        this.input = input;
        this.leftReagent = leftReagent;
        this.rightReagent = rightReagent;
        this.output = output;
        this.ticks = ticks;
    }

    @Override
    public int getBaseEnergyCost() {
        return 0;
    }

    @Override
    public List<OutputStack> craft(Input input, RegistryAccess registryAccess) {

        double modifier = getModifier(input.getItem(0), leftReagent);
        modifier *= getModifier(input.getItem(1), rightReagent);

        return List.of(OutputStack.of(new FluidStack(output.getFluid(), (int) (output.getAmount() * modifier))));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output().copy()));
    }

    @Override
    public boolean matches(Input input, Level level) {
        FluidStack inputTank = input.getInputTank().getFluid();
        if (!this.input.test(inputTank) || inputTank.getAmount() < this.input.amount()) {
            return false;
        }

        return input.getItem(0).is(leftReagent) && input.getItem(1).is(rightReagent);
    }

    public static double getModifier(ItemStack stack, TagKey<Item> reagent) {
        var map = stack.getItemHolder().getData(VatReagent.DATA_MAP);
        if (map != null) {
            return map.getOrDefault(reagent, 1D);
        }
        return 1;
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return EIORecipes.VAT_FERMENTING.serializer().get();
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipes.VAT_FERMENTING.type().get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.FERMENTING.get();
    }

    public SizedFluidIngredient input() {
        return input;
    }

    public TagKey<Item> leftReagent() {
        return leftReagent;
    }

    public TagKey<Item> rightReagent() {
        return rightReagent;
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
            var leftIngredient = Ingredient.of(BuiltInRegistries.ITEM.get(leftReagent).orElseThrow());
            var rightIngredient = Ingredient.of(BuiltInRegistries.ITEM.get(rightReagent).orElseThrow());
            placementInfo = PlacementInfo.create(List.of(leftIngredient, rightIngredient));
        }
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new FermentingDisplay(
            input.ingredient().display(),
            new SlotDisplay.TagSlotDisplay(leftReagent),
            new SlotDisplay.TagSlotDisplay(rightReagent),
            new FluidStackSlotDisplay(output),
            new SlotDisplay.ItemSlotDisplay(EIOBlocks.VAT.asItem())
            ));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (FermentingRecipe) obj;
        return Objects.equals(this.input, that.input) && Objects.equals(this.leftReagent, that.leftReagent) && Objects.equals(this.rightReagent, that.rightReagent) && Objects.equals(this.output,
            that.output) && this.ticks == that.ticks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, leftReagent, rightReagent, output, ticks);
    }

    @Override
    public String toString() {
        return "FermentingRecipe[" + "input=" + input + ", " + "leftReagent=" + leftReagent + ", " + "rightReagent=" + rightReagent + ", " + "output=" + output
            + ", " + "ticks=" + ticks + ']';
    }

    public record Input(ItemStack leftReagent, ItemStack rightStack, MachineFluidTank inputTank) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
                case 0 -> leftReagent;
                case 1 -> rightStack;
                default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public int size() {
            return 2;
        }

        public MachineFluidTank getInputTank() {
            return inputTank;
        }

    }

    public record FermentingDisplay(SlotDisplay fluid, SlotDisplay left, SlotDisplay right, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        public static final MapCodec<FermentingDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_379634_ -> p_379634_.group(
                    SlotDisplay.CODEC.fieldOf("ingredients").forGetter(FermentingDisplay::fluid),
                    SlotDisplay.CODEC.fieldOf("left").forGetter(FermentingDisplay::result),
                    SlotDisplay.CODEC.fieldOf("right").forGetter(FermentingDisplay::result),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(FermentingDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FermentingDisplay::craftingStation)
                )
                .apply(p_379634_, FermentingDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            FermentingDisplay::fluid,
            SlotDisplay.STREAM_CODEC,
            FermentingDisplay::left,
            SlotDisplay.STREAM_CODEC,
            FermentingDisplay::right,
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
            if (!left().isEnabled(flagSet)) {
                return false;
            }
            if (!right().isEnabled(flagSet)) {
                return false;
            }
            if (!result().isEnabled(flagSet)) {
                return false;
            }
            return RecipeDisplay.super.isEnabled(flagSet);
        }
    }

    public static class Serializer implements RecipeSerializer<FermentingRecipe> {
        private static final StreamCodec<ByteBuf, TagKey<Item>> ITEM_TAG_STREAM_CODEC = Identifier.STREAM_CODEC.map(loc -> TagKey.create(Registries.ITEM, loc),
            TagKey::location);

        public static final MapCodec<FermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                .group(SizedFluidIngredient.CODEC.fieldOf("input").forGetter(FermentingRecipe::input), //TODO make sure this handles empty
                    TagKey.codec(Registries.ITEM).fieldOf("left_reagent").forGetter(FermentingRecipe::leftReagent),
                    TagKey.codec(Registries.ITEM).fieldOf("right_reagent").forGetter(FermentingRecipe::rightReagent),
                    FluidStack.CODEC.fieldOf("output").forGetter(FermentingRecipe::output), Codec.INT.fieldOf("ticks").forGetter(FermentingRecipe::ticks))
                .apply(instance, FermentingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> STREAM_CODEC = StreamCodec.composite(SizedFluidIngredient.STREAM_CODEC,
            FermentingRecipe::input, ITEM_TAG_STREAM_CODEC, FermentingRecipe::leftReagent, ITEM_TAG_STREAM_CODEC, FermentingRecipe::rightReagent, FluidStack.STREAM_CODEC, FermentingRecipe::output, ByteBufCodecs.INT, FermentingRecipe::ticks,
            FermentingRecipe::new);

        @Override
        public MapCodec<FermentingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
