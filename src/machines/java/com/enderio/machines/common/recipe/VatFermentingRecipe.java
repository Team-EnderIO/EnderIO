package com.enderio.machines.common.recipe;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class VatFermentingRecipe implements MachineRecipe<VatFermentingRecipe.Container> {

    private final ResourceLocation id;
    private final Fluid inputFluid;
    private final Fluid outputFluid;
    private final ResourceLocation leftReagentType;
    private final ResourceLocation rightReagentType;
    private final int ticks;

    public VatFermentingRecipe(ResourceLocation id, Fluid inputFluid, Fluid outputFluid, ResourceLocation leftReagent, ResourceLocation rightReagent,
        int ticks) {
        this.id = id;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.leftReagentType = leftReagent;
        this.rightReagentType = rightReagent;
        this.ticks = ticks;
    }

    @Override
    public int getBaseEnergyCost() {
        return 0;
    }

    @Override
    public List<OutputStack> craft(Container container, RegistryAccess registryAccess) {
        return List.of(OutputStack.EMPTY);
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.EMPTY);
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (!container.getTank().getFluidInTank(0).getFluid().isSame(inputFluid)) {
            return false;
        }

        // TODO: Use caches
        Optional<VatReagentRecipe> left = level
            .getRecipeManager()
            .getRecipeFor(MachineRecipes.VAT_REAGENT.type().get(), new VatReagentRecipe.Container(container.getItem(0), leftReagentType), level);
        if (left.isEmpty()) {
            return false;
        }

        Optional<VatReagentRecipe> right = level
            .getRecipeManager()
            .getRecipeFor(MachineRecipes.VAT_REAGENT.type().get(), new VatReagentRecipe.Container(container.getItem(1), rightReagentType), level);
        if (right.isEmpty()) {
            return false;
        }

        return true;
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
            String input = serializedRecipe.get("input").getAsString();
            Fluid inputFluid = BuiltInRegistries.FLUID
                .getOptional(ResourceLocation.tryParse(input))
                .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + input + "'"));
            String output = serializedRecipe.get("output").getAsString();
            Fluid outputFluid = BuiltInRegistries.FLUID
                .getOptional(ResourceLocation.tryParse(output))
                .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + output + "'"));
            ResourceLocation leftModifier = new ResourceLocation(serializedRecipe.get("leftReagent").getAsString());
            ResourceLocation rightModifier = new ResourceLocation(serializedRecipe.get("rightReagent").getAsString());
            int ticks = serializedRecipe.get("ticks").getAsInt();
            return new VatFermentingRecipe(recipeId, inputFluid, outputFluid, leftModifier, rightModifier, ticks);
        }

        @Override
        public @Nullable VatFermentingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, VatFermentingRecipe recipe) {

        }
    }
}
