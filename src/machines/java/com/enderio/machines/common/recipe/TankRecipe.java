package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.EnderRecipe;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TankRecipe implements EnderRecipe<TankRecipe.Container> {

    private final ResourceLocation id;
    private final Ingredient input;
    private final Item output;
    private final FluidStack fluid;
    private final boolean isEmptying;

    public TankRecipe(ResourceLocation id, Ingredient input, Item output, FluidStack fluid, boolean isEmptying) {
        this.id = id;
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
            if (pContainer.getFluidTank().fill(fluid, IFluidHandler.FluidAction.SIMULATE) <= 0)
                return false;
            return input.test(FluidTankBlockEntity.FLUID_FILL_INPUT.getItemStack(pContainer));
        }
        if (pContainer.getFluidTank().drain(fluid, IFluidHandler.FluidAction.SIMULATE).isEmpty())
            return false;
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
    public ResourceLocation getId() {
        return id;
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

        @Override
        public TankRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            Ingredient input = Ingredient.fromJson(serializedRecipe.get("input").getAsJsonObject());

            ResourceLocation id = new ResourceLocation(serializedRecipe.get("output").getAsString());
            Item output = ForgeRegistries.ITEMS.getValue(id);

            JsonObject fluidJson = serializedRecipe.get("fluid").getAsJsonObject();
            ResourceLocation fluidId = new ResourceLocation(fluidJson.get("fluid").getAsString());
            FluidStack fluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidId), fluidJson.get("amount").getAsInt());

            boolean isEmptying = serializedRecipe.get("is_emptying").getAsBoolean();

            return new TankRecipe(recipeId, input, output, fluid, isEmptying);
        }

        @Override
        public @Nullable TankRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                Ingredient input = Ingredient.fromNetwork(buffer);

                ResourceLocation outputId = buffer.readResourceLocation();
                Item output = ForgeRegistries.ITEMS.getValue(outputId);
                if (output == null) {
                    throw new ResourceLocationException("The output of recipe " + recipeId + " does not exist.");
                }

                FluidStack fluid = FluidStack.readFromPacket(buffer);

                boolean isEmptying = buffer.readBoolean();

                return new TankRecipe(recipeId, input, output, fluid, isEmptying);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading tank recipe from packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TankRecipe recipe) {
            try {
                recipe.input.toNetwork(buffer);
                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.output)));
                recipe.fluid.writeToPacket(buffer);
                buffer.writeBoolean(recipe.isEmptying);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing tank recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
