package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.EnderRecipe;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TankRecipe implements EnderRecipe<TankRecipe.Container> {

    final Ingredient input;
    final Item output;
    final FluidStack fluid;
    final boolean isEmptying;

    public TankRecipe(Ingredient input, Item output, FluidStack fluid, boolean isEmptying) {
        this.input = input;
        this.output = output;
        this.fluid = fluid;
        this.isEmptying = isEmptying;
    }

    public Ingredient getInput() {
        return input;
    }

    public Item getOutput() {
        return output;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public boolean isEmptying() {
        return isEmptying;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (isEmptying) {
            if (pContainer.getFluidTank().fill(fluid, IFluidHandler.FluidAction.SIMULATE) <= 0) {
                return false;
            }

            return input.test(FluidTankBlockEntity.FLUID_FILL_INPUT.getItemStack(pContainer));
        }

        if (pContainer.getFluidTank().drain(fluid, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
            return false;
        }

        return input.test(FluidTankBlockEntity.FLUID_DRAIN_INPUT.getItemStack(pContainer));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return null;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return new ItemStack(output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.TANK.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.TANK.type().get();
    }

    public static class Container extends RecipeWrapper {

        private final FluidTank fluidTank;

        public Container(IItemHandlerModifiable inv, FluidTank fluidTank) {
            super(inv);
            this.fluidTank = fluidTank;
        }

        public FluidTank getFluidTank() {
            return fluidTank;
        }
    }

    public static class Serializer implements RecipeSerializer<TankRecipe> {

        private static final Codec<TankRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(recipe -> recipe.output),
            FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid),
            Codec.BOOL.fieldOf("is_emptying").forGetter(recipe -> recipe.isEmptying)
        ).apply(instance, TankRecipe::new));

        @Override
        public Codec<TankRecipe> codec() {
            return CODEC;
        }

        @Override
        public @Nullable TankRecipe fromNetwork(FriendlyByteBuf buffer) {
            try {
                Ingredient input = Ingredient.fromNetwork(buffer);

                ResourceLocation outputId = buffer.readResourceLocation();
                Item output = BuiltInRegistries.ITEM.get(outputId);
                if (output == Items.AIR) {
                    return null;
                }

                FluidStack fluid = FluidStack.readFromPacket(buffer);

                boolean isEmptying = buffer.readBoolean();

                return new TankRecipe(input, output, fluid, isEmptying);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading tank recipe from packet.", ex);
                return null;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TankRecipe recipe) {
            try {
                recipe.input.toNetwork(buffer);
                buffer.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(recipe.output)));
                recipe.fluid.writeToPacket(buffer);
                buffer.writeBoolean(recipe.isEmptying);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing tank recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
