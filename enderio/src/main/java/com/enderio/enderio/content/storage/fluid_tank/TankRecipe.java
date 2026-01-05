package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlagSet;
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
import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

public final class TankRecipe implements Recipe<TankRecipe.Input> {
    private final Ingredient input;
    private final ItemStack output;
    private final FluidStack fluid;
    private final Mode mode;

    @Nullable
    private PlacementInfo placementInfo;

    public TankRecipe(Ingredient input, ItemStack output, FluidStack fluid, Mode mode) {
        this.input = input;
        this.output = output;
        this.fluid = fluid;
        this.mode = mode;
    }

    public Ingredient input() {
        return input;
    }

    public ItemStack output() {
        return output;
    }

    public FluidStack fluid() {
        return fluid;
    }

    public Mode mode() {
        return mode;
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        switch (mode) {
        case FILL -> {
            if (!FluidStack.matches(recipeInput.fluidContents, fluid) || recipeInput.fluidContents.getAmount() < fluid.getAmount()) {
                return false;
            }

            return input.test(recipeInput.fillItem);
        }
        case EMPTY -> {
            if (!recipeInput.fluidContents.isEmpty() && !FluidStack.matches(recipeInput.fluidContents, fluid)) {
                return false;
            }

            if (recipeInput.fluidContents.getAmount() + fluid.getAmount() > recipeInput.tankCapacity) {
                return false;
            }

            return input.test(recipeInput.emptyItem);
        }
        default -> throw new NotImplementedException();
        }
    }

    @Override
    public ItemStack assemble(Input recipeInput, HolderLookup.Provider lookupProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new TankDisplay(input.display(), new SlotDisplay.ItemStackSlotDisplay(output.copy()), new SlotDisplay.ItemSlotDisplay(EIOBlocks.FLUID_TANK_ITEM)));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.TANK.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return EIORecipes.TANK.serializer().get();
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipes.TANK.type().get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(input);
        }

        return placementInfo;
    }

    public enum Mode implements StringRepresentable {
        FILL(0, "fill"), EMPTY(1, "empty");

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        public static final IntFunction<Mode> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, Mode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

        private final int id;
        private final String name;

        Mode(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public record Input(ItemStack fillItem, ItemStack emptyItem, FluidStack fluidContents, int tankCapacity) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
                case 0 -> fillItem;
                case 1 -> emptyItem;
                default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public int size() {
            return 2;
        }
    }

    public record TankDisplay(SlotDisplay ingredient, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        public static final MapCodec<TankDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(p_379634_ -> p_379634_
            .group(SlotDisplay.CODEC.fieldOf("ingredients").forGetter(TankDisplay::ingredient), SlotDisplay.CODEC.fieldOf("result").forGetter(TankDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(TankDisplay::craftingStation))
            .apply(p_379634_, TankDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, TankDisplay> STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC,
            TankDisplay::ingredient, SlotDisplay.STREAM_CODEC, TankDisplay::result, SlotDisplay.STREAM_CODEC, TankDisplay::craftingStation, TankDisplay::new);
        public static final Type<TankDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flagSet) {
            return this.ingredient.isEnabled(flagSet) && RecipeDisplay.super.isEnabled(flagSet);
        }
    }

    public static class Serializer implements RecipeSerializer<TankRecipe> {

        private static final MapCodec<TankRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Ingredient.CODEC.fieldOf("input").forGetter(TankRecipe::input), ItemStack.CODEC.fieldOf("output").forGetter(TankRecipe::output),
                FluidStack.CODEC.fieldOf("fluid").forGetter(TankRecipe::fluid), Mode.CODEC.fieldOf("mode").forGetter(TankRecipe::mode))
            .apply(instance, TankRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TankRecipe> STREAM_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, TankRecipe::input, ItemStack.STREAM_CODEC, TankRecipe::output,
            FluidStack.STREAM_CODEC, TankRecipe::fluid, Mode.STREAM_CODEC, TankRecipe::mode, TankRecipe::new);

        @Override
        public MapCodec<TankRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TankRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
