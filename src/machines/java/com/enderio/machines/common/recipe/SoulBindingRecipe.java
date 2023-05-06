package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.api.capability.IEntityStorage;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SoulBindingRecipe implements MachineRecipe<SoulBindingRecipe.Container>{

    private final ResourceLocation id;
    private final Item output;
    private final List<Ingredient> inputs;
    private final int exp;
    @Nullable
    private final ResourceLocation entityType;
    private final int energy;


    public SoulBindingRecipe(ResourceLocation id, Item output, List<Ingredient> inputs, int energy, int exp, @Nullable ResourceLocation entityType) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.energy = energy;
        this.exp = exp;
        this.entityType = entityType;
    }

    @Override
    public int getEnergyCost(SoulBindingRecipe.Container container) {
        return energy;
    }

    public int getExpCost() {
        return exp;
    }

    @Override
    public List<OutputStack> craft(SoulBindingRecipe.Container container) {
        ItemStack vial = container.getItem(0);
        List<OutputStack> results = getResultStacks();
        results.forEach(o -> {
            ItemStack result = o.getItem(); //TODO will this auto update since the stack is updated?
            vial.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(inputEntity -> {
                result.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(resultEntity -> {
                    resultEntity.setStoredEntityData(inputEntity.getStoredEntityData());
                });
            });
        });
        return results;
    }

    @Override
    public List<OutputStack> getResultStacks() {
        return List.of(OutputStack.of(new ItemStack(output, 1)));
    }

    @Override
    public boolean matches(SoulBindingRecipe.Container container, Level pLevel) {
        container.setNeededXP(0);
        for (int i = 0; i < inputs.size(); i++) { //Items match
            if (!inputs.get(i).test(container.getItem(i)))
                return false;
        }
        LazyOptional<IEntityStorage> capability = container.getItem(0).getCapability(EIOCapabilities.ENTITY_STORAGE);
        if (!capability.isPresent()) { //vial (or other entity storage
            return false;
        }
        if (entityType == null) { //type doesn't matter
            container.setNeededXP(exp);
            return ExperienceUtil.getLevelFromFluid(container.getFluidTank().getFluidAmount()) >= exp;
        }
        IEntityStorage storage = capability.resolve().get();
        if (storage.hasStoredEntity() && storage.getStoredEntityData().getEntityType().get().equals(entityType)) { //type matters
            container.setNeededXP(exp);
            return ExperienceUtil.getLevelFromFluid(container.getFluidTank().getFluidAmount()) >= exp;
        }
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.SOUL_BINDING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.SOUL_BINDING.type().get();
    }

    public static class Container extends RecipeWrapper {

        private final FluidTank fluidTank;
        private int neededXP;
        public Container(IItemHandlerModifiable inv, FluidTank fluidTank) {
            super(inv);
            this.fluidTank = fluidTank;
        }

        public FluidTank getFluidTank() {
            return fluidTank;
        }

        public void setNeededXP(int neededXP) {
            this.neededXP = neededXP;
        }

        public int getNeededXP() {
            return neededXP;
        }
    }

    public static class Serializer implements RecipeSerializer<SoulBindingRecipe> {

        @Override
        public SoulBindingRecipe fromJson(ResourceLocation pRecipeId, JsonObject serializedRecipe) {

            ResourceLocation id = new ResourceLocation(serializedRecipe.get("output").getAsString());
            Item output = ForgeRegistries.ITEMS.getValue(id);

            List<Ingredient> inputs = new ArrayList<>();
            JsonArray inputsJson = serializedRecipe.getAsJsonArray("inputs");
            for (JsonElement itemJson : inputsJson) {
                inputs.add(Ingredient.fromJson(itemJson));
            }

            int energy = serializedRecipe.get("energy").getAsInt();
            int exp = serializedRecipe.get("exp").getAsInt();

            ResourceLocation entityType = null;
            if (serializedRecipe.has("entitytype")) {
                entityType = new ResourceLocation(serializedRecipe.get("entitytype").getAsString());
            }

            return new SoulBindingRecipe(pRecipeId, output, inputs, energy, exp, entityType);
        }

        @Nullable
        @Override
        public SoulBindingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                ResourceLocation outputId = buffer.readResourceLocation();
                Item output = ForgeRegistries.ITEMS.getValue(outputId);
                if (output == null) {
                    throw new ResourceLocationException("The output of recipe " + recipeId + " does not exist.");
                }
                List<Ingredient> inputs = buffer.readCollection(ArrayList::new, Ingredient::fromNetwork);
                int energy = buffer.readInt();
                int exp = buffer.readInt();
                ResourceLocation entityType = null;
                try { //fails if not present
                    buffer.readResourceLocation();
                } catch (Exception ignored) {

                }

                return new SoulBindingRecipe(recipeId, output, inputs, energy, exp, entityType);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading soul binding recipe from packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SoulBindingRecipe recipe) {
            try {
                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.output)));
                buffer.writeCollection(recipe.inputs, (buf, ing) -> ing.toNetwork(buf));
                buffer.writeInt(recipe.energy);
                buffer.writeInt(recipe.exp);
                if (recipe.entityType != null) { //don't write null
                    buffer.writeResourceLocation(recipe.entityType);
                }
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing soul binding recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
