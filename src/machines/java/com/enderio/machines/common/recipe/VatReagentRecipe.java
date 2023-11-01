package com.enderio.machines.common.recipe;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class VatReagentRecipe implements MachineRecipe<VatReagentRecipe.Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final float multiplier;
    private final ResourceLocation modifier;

    public VatReagentRecipe(ResourceLocation id, Ingredient input, float multiplier, ResourceLocation modifier) {
        this.id = id;
        this.input = input;
        this.multiplier = multiplier;
        this.modifier = modifier;
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
        return input.test(container.getItem(0)) && container.modifier.equals(this.modifier);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.VAT_REAGENT.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.VAT_REAGENT.type().get();
    }

    public static class Container extends RecipeWrapper {

        private final ResourceLocation modifier;

        public Container(ItemStack stack, ResourceLocation modifier) {
            super(new ItemStackHandler(1));
            inv.setStackInSlot(0, stack);
            this.modifier = modifier;
        }

        public ResourceLocation getModifier() {
            return modifier;
        }
    }

    public static class Serializer implements RecipeSerializer<VatReagentRecipe> {

        @Override
        public VatReagentRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            Ingredient input = Ingredient.fromJson(serializedRecipe.getAsJsonObject("input"));
            float multiplier = serializedRecipe.get("multiplier").getAsFloat();
            ResourceLocation modifier = new ResourceLocation(serializedRecipe.get("modifier").getAsString());
            return new VatReagentRecipe(recipeId, input, multiplier, modifier);
        }

        @Override
        public @Nullable VatReagentRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            float multiplier = buffer.readFloat();
            ResourceLocation modifier = buffer.readResourceLocation();
            return new VatReagentRecipe(recipeId, input, multiplier, modifier);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, VatReagentRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeFloat(recipe.multiplier);
            buffer.writeResourceLocation(recipe.modifier);
        }
    }
}
