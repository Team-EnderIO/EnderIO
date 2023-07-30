package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.api.capability.IEntityStorage;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SoulBindingRecipe implements MachineRecipe<SoulBindingRecipe.Container> {

    private final ResourceLocation id;
    private final Item output;
    private final Ingredient input;
    private final int exp;
    @Nullable private final ResourceLocation entityType;
    @Nullable private final MobCategory mobCategory;
    private final int energy;

    public SoulBindingRecipe(ResourceLocation id, Item output, Ingredient input, int energy, int exp, @Nullable ResourceLocation entityType,
        @Nullable MobCategory mobCategory) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.energy = energy;
        this.exp = exp;

        if (entityType != null && mobCategory != null) {
            throw new IllegalStateException("entityType and mobCategory are mutually exclusive!");
        }

        this.entityType = entityType;
        this.mobCategory = mobCategory;
    }

    public Ingredient getInput() {
        return input;
    }

    @Nullable
    public ResourceLocation getEntityType() {
        return entityType;
    }

    @Nullable
    public MobCategory getMobCategory() {
        return mobCategory;
    }

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    public int getExpCost() {
        return exp;
    }

    @Override
    public List<OutputStack> craft(SoulBindingRecipe.Container container, RegistryAccess registryAccess) {
        ItemStack vial = container.getItem(0);
        List<OutputStack> results = getResultStacks(registryAccess);
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
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(new ItemStack(output, 1)));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(SoulBindingRecipe.Container container, Level pLevel) {
        container.setNeededXP(0);
        if (!container.getItem(0).is(EIOItems.FILLED_SOUL_VIAL.get()))
            return false;
        if (!input.test(container.getItem(1)))
            return false;
        LazyOptional<IEntityStorage> capability = container.getItem(0).getCapability(EIOCapabilities.ENTITY_STORAGE);
        if (!capability.isPresent()) { //vial (or other entity storage
            return false;
        }
        if (entityType == null && mobCategory == null) { //type doesn't matter
            container.setNeededXP(exp);
            return ExperienceUtil.getLevelFromFluid(container.getFluidTank().getFluidAmount()) >= exp;
        }
        IEntityStorage storage = capability.resolve().get();
        if (storage.hasStoredEntity()) {
            var type = storage.getStoredEntityData().getEntityType();
            if (!type.isPresent())
                return false;

            var entityType = ForgeRegistries.ENTITY_TYPES.getValue(type.get());
            if (entityType == null)
                return false;

            if (entityType.getCategory().equals(mobCategory)) {
                container.setNeededXP(exp);
                return ExperienceUtil.getLevelFromFluid(container.getFluidTank().getFluidAmount()) >= exp;
            }
        }
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

            Ingredient input = Ingredient.fromJson(serializedRecipe.get("input").getAsJsonObject());

            int energy = serializedRecipe.get("energy").getAsInt();
            int exp = serializedRecipe.get("exp").getAsInt();

            ResourceLocation entityType = null;
            if (serializedRecipe.has("entity_type")) {
                entityType = new ResourceLocation(serializedRecipe.get("entity_type").getAsString());
            }

            MobCategory mobCategory = null;
            if (serializedRecipe.has("mob_category")) {
                mobCategory = MobCategory.byName(serializedRecipe.get("mob_category").getAsString());
            }

            return new SoulBindingRecipe(pRecipeId, output, input, energy, exp, entityType, mobCategory);
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
                Ingredient input = Ingredient.fromNetwork(buffer);
                int energy = buffer.readInt();
                int exp = buffer.readInt();

                ResourceLocation entityType = null;
                if (buffer.readBoolean()) {
                    entityType = buffer.readResourceLocation();
                }

                MobCategory mobCategory = null;
                if (buffer.readBoolean()) {
                    mobCategory = MobCategory.byName(buffer.readUtf());
                }

                return new SoulBindingRecipe(recipeId, output, input, energy, exp, entityType, mobCategory);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading soul binding recipe from packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SoulBindingRecipe recipe) {
            try {
                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.output)));
                recipe.input.toNetwork(buffer);
                buffer.writeInt(recipe.energy);
                buffer.writeInt(recipe.exp);

                buffer.writeBoolean(recipe.entityType != null);
                if (recipe.entityType != null) { //don't write null
                    buffer.writeResourceLocation(recipe.entityType);
                }

                buffer.writeBoolean(recipe.mobCategory != null);
                if (recipe.mobCategory != null) { //don't write null
                    buffer.writeUtf(recipe.mobCategory.getName());
                }
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing soul binding recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
