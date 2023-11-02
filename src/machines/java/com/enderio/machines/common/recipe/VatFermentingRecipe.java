package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class VatFermentingRecipe implements MachineRecipe<VatFermentingRecipe.Container> {

    private final ResourceLocation id;
    private final FluidStack inputFluid;
    private final FluidStack outputFluid;
    private final int baseModifier;
    private final ResourceLocation leftReagent;
    private final ResourceLocation rightReagent;
    private final int ticks;

    public VatFermentingRecipe(ResourceLocation id, FluidStack inputFluid, FluidStack outputFluid, int baseModifier, ResourceLocation leftReagent,
            ResourceLocation rightReagent,
        int ticks) {
        this.id = id;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.baseModifier = baseModifier;
        this.leftReagent = leftReagent;
        this.rightReagent = rightReagent;
        this.ticks = ticks;
    }

    @Override
    public int getBaseEnergyCost() {
        return 0;
    }

    @Override
    public List<OutputStack> craft(Container container, RegistryAccess registryAccess) {
        return List.of(OutputStack.of(outputFluid));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.EMPTY);
    }

    @Override
    public boolean matches(Container container, Level level) {
        FluidStack inputTank = container.getTank().getFluidInTank(0);
        if (!inputTank.isFluidEqual(inputFluid) || !(inputTank.getAmount() < inputFluid.getAmount())) {
            return false;
        }

        // TODO: Use caches
        Optional<VatReagentRecipe> left = level
                .getRecipeManager()
                .getRecipeFor(MachineRecipes.VAT_REAGENT.type().get(), new VatReagentRecipe.Container(container.getItem(0), leftReagent), level);
        if (left.isEmpty()) {
            return false;
        }

        Optional<VatReagentRecipe> right = level
            .getRecipeManager()
                .getRecipeFor(MachineRecipes.VAT_REAGENT.type().get(), new VatReagentRecipe.Container(container.getItem(1), rightReagent), level);
        if (right.isEmpty()) {
            return false;
        }

        return true;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.VAT_FERMENTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.VAT_FERMENTING.type().get();
    }

    public int getTicks() {
        return ticks;
    }

    public static class Container extends RecipeWrapper {

        private final FluidTank tank;

        public Container(IItemHandlerModifiable inv, FluidTank tank) {
            super(inv);
            this.tank = tank;
        }

        public FluidTank getTank() {
            return tank;
        }
    }

    public static class Serializer implements RecipeSerializer<VatFermentingRecipe> {

        @Override
        public VatFermentingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {

            JsonObject inputFluidJson = serializedRecipe.get("input").getAsJsonObject();
            ResourceLocation inputId = new ResourceLocation(inputFluidJson.get("fluid").getAsString());
            FluidStack inputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(inputId), inputFluidJson.get("amount").getAsInt());

            JsonObject outputFluidJson = serializedRecipe.get("output").getAsJsonObject();
            ResourceLocation outputId = new ResourceLocation(outputFluidJson.get("fluid").getAsString());
            FluidStack outputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(outputId), outputFluidJson.get("amount").getAsInt());

            int baseModifier = serializedRecipe.get("baseModifier").getAsInt();
            ResourceLocation leftReagent = new ResourceLocation(serializedRecipe.get("leftReagent").getAsString());
            ResourceLocation rightReagent = new ResourceLocation(serializedRecipe.get("rightReagent").getAsString());
            int ticks = serializedRecipe.get("ticks").getAsInt();

            return new VatFermentingRecipe(recipeId, inputFluid, outputFluid, baseModifier, leftReagent, rightReagent, ticks);
        }

        @Override
        public @Nullable VatFermentingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                FluidStack inputFluid = FluidStack.readFromPacket(buffer);
                FluidStack outputFluid = FluidStack.readFromPacket(buffer);
                int baseModifier = buffer.readInt();
                ResourceLocation leftReagent = buffer.readResourceLocation();
                ResourceLocation rightReagent = buffer.readResourceLocation();
                int ticks = buffer.readInt();
                return new VatFermentingRecipe(recipeId, inputFluid, outputFluid, baseModifier, leftReagent, rightReagent, ticks);

            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading Vat recipe for packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, VatFermentingRecipe recipe) {
            try {
                recipe.inputFluid.writeToPacket(buffer);
                recipe.outputFluid.writeToPacket(buffer);
                buffer.writeInt(recipe.baseModifier);
                buffer.writeResourceLocation(recipe.leftReagent);
                buffer.writeResourceLocation(recipe.rightReagent);
                buffer.writeInt(recipe.ticks);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading Vat recipe for packet.", ex);
                throw ex;
            }

        }
    }
}
