package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.enderio.foundation.io.fluid.MachineFluidTank;
import com.enderio.enderio.foundation.util.SizedFluidIngredientHelper;
import com.enderio.enderio.init.EIOConduitTypes;
import com.enderio.enderio.init.EIORecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.function.IntFunction;

public record TankRecipe(Ingredient input, ItemStack output, SizedFluidIngredient fluid, Mode mode)
        implements Recipe<TankRecipe.Input> {

    public enum Mode implements StringRepresentable {
        FILL(0, "fill"), EMPTY(1, "empty");

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        public static final IntFunction<Mode> BY_ID = ByIdMap.continuous(key -> key.id, values(),
                ByIdMap.OutOfBoundsStrategy.ZERO);
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

    @Override
    public boolean matches(Input recipeInput, Level level) {
        // If we have no input -or- output fluid, no matches allowed.
        List<FluidStack> possibleFluids = SizedFluidIngredientHelper.getFluidStacksInPreferredOrder(fluid);
        if (possibleFluids.isEmpty()) {
            return false;
        }

        switch (mode) {
        case FILL -> {
            FluidStack drainSimulation = recipeInput.fluidTank().drain(fluid.amount(), IFluidHandler.FluidAction.SIMULATE);
            if (!fluid.test(drainSimulation) || drainSimulation.getAmount() < fluid.amount()) {
                return false;
            }

            return input.test(recipeInput.fillItem);
        }
        case EMPTY -> {
            if (!input.test(recipeInput.emptyItem)) {
                return false;
            }

            for (FluidStack testFluid : possibleFluids) {
                FluidStack testFill = testFluid.copyWithAmount(fluid.amount());
                if (recipeInput.fluidTank().fill(testFill, IFluidHandler.FluidAction.SIMULATE) == fluid.amount()) {
                    // We can fill this fluid type into the tank.
                    return true;
                }
            }

            return false;
        }
        default -> throw new NotImplementedException();
        }
    }

    @Override
    public ItemStack assemble(Input recipeInput, HolderLookup.Provider lookupProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return output.copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EIORecipes.TANK.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return EIORecipes.TANK.type().get();
    }

    public record Input(ItemStack fillItem, ItemStack emptyItem, MachineFluidTank fluidTank) implements RecipeInput {

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

    public static class Serializer implements RecipeSerializer<TankRecipe> {

        private static final MapCodec<TankRecipe> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(TankRecipe::input),
                            ItemStack.CODEC.fieldOf("output").forGetter(TankRecipe::output),
                            SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(TankRecipe::fluid),
                            Mode.CODEC.fieldOf("mode").forGetter(TankRecipe::mode))
                    .apply(instance, TankRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TankRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, TankRecipe::input, ItemStack.STREAM_CODEC, TankRecipe::output,
                SizedFluidIngredient.STREAM_CODEC, TankRecipe::fluid, Mode.STREAM_CODEC, TankRecipe::mode, TankRecipe::new);

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
