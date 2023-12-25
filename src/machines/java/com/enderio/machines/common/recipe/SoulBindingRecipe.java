package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.api.capability.IEntityStorage;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.SoulBinderBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.souldata.SoulDataReloadListener;
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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class SoulBindingRecipe implements MachineRecipe<SoulBindingRecipe.Container> {

    private final ResourceLocation id;
    private final Item output;
    private final Ingredient input;
    private final int energy;
    private final int exp;
    @Nullable private final ResourceLocation entityType;
    @Nullable private final MobCategory mobCategory;
    @Nullable private final String souldata;

    public SoulBindingRecipe(ResourceLocation id, Item output, Ingredient input, int energy, int exp, @Nullable ResourceLocation entityType,
        @Nullable MobCategory mobCategory, @Nullable String souldata) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.energy = energy;
        this.exp = exp;

        if (entityType != null && mobCategory != null) {
            throw new IllegalStateException("entityType and mobCategory are mutually exclusive!");
        }

        if (souldata != null && mobCategory != null) {
            throw new IllegalStateException("souldata and mobCategory are mutually exclusive!");
        }

        if (entityType != null && souldata != null) {
            throw new IllegalStateException("entityType and souldata are mutually exclusive!");
        }

        this.entityType = entityType;
        this.mobCategory = mobCategory;
        this.souldata = souldata;
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

    @Nullable
    public String getSouldata() {
        return souldata;
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
        ItemStack vial = SoulBinderBlockEntity.INPUT_SOUL.getItemStack(container);
        List<OutputStack> results = getResultStacks(registryAccess);
        ItemStack result = results.get(0).getItem();
        vial.getCapability(EIOCapabilities.ENTITY_STORAGE)
            .ifPresent(inputEntity -> result.getCapability(EIOCapabilities.ENTITY_STORAGE)
                .ifPresent(resultEntity -> resultEntity.setStoredEntityData(inputEntity.getStoredEntityData())));
        return results;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.getDefaultInstance()), OutputStack.of(EIOItems.EMPTY_SOUL_VIAL.asStack()));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(SoulBindingRecipe.Container container, Level pLevel) {
        if (!container.getItem(0).is(EIOItems.FILLED_SOUL_VIAL.get())) {
            return false;
        }

        if (!input.test(SoulBinderBlockEntity.INPUT_OTHER.getItemStack(container))) {
            return false;
        }

        LazyOptional<IEntityStorage> capability = container.getItem(0).getCapability(EIOCapabilities.ENTITY_STORAGE);
        if (!capability.isPresent()) { //vial (or other entity storage)
            return false;
        }

        if (souldata != null) { //is in the selected souldata
            if (SoulDataReloadListener.fromString(souldata).matches(
                container.getItem(0).getCapability(EIOCapabilities.ENTITY_STORAGE).resolve().get()
                    .getStoredEntityData().getEntityType().get()).isEmpty()) {
                return false;
            }

            return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= exp;
        }

        if (mobCategory == null && entityType == null) { //No souldata, entity type or mob category
            return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= exp;
        }

        IEntityStorage storage = capability.resolve().get();
        if (storage.hasStoredEntity()) {
            var type = storage.getStoredEntityData().getEntityType();
            if (type.isEmpty()) {
                return false;
            }

            var entityType = ForgeRegistries.ENTITY_TYPES.getValue(type.get());
            if (entityType == null) {
                return false;
            }

            if (entityType.getCategory().equals(mobCategory)) {
                return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= exp;
            }
        }
        //type matters
        if (storage.hasStoredEntity() && storage.getStoredEntityData().getEntityType().get().equals(entityType)) {
            return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= exp;
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

        private final Supplier<Integer> fluid;

        public Container(IItemHandlerModifiable inv, Supplier<Integer> fluid) {
            super(inv);
            this.fluid = fluid;
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

            String souldata = null;
            if (serializedRecipe.has("souldata")) {
                souldata = serializedRecipe.get("souldata").getAsString();
            }

            return new SoulBindingRecipe(pRecipeId, output, input, energy, exp, entityType, mobCategory, souldata);
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
                int mode = buffer.readInt();

                ResourceLocation entityType = null;
                if (mode == 1) {
                    entityType = buffer.readResourceLocation();
                }

                MobCategory mobCategory = null;
                if (mode == 2) {
                    mobCategory = MobCategory.byName(buffer.readUtf());
                }

                String souldata = null;
                if (mode == 3) {
                    souldata = buffer.readUtf();
                }

                return new SoulBindingRecipe(recipeId, output, input, energy, exp, entityType, mobCategory, souldata);
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

                if (recipe.entityType != null) { //don't write null
                    buffer.writeInt(1);
                    buffer.writeResourceLocation(recipe.entityType);
                } else if (recipe.mobCategory != null) { //don't write null
                    buffer.writeInt(2);
                    buffer.writeUtf(recipe.mobCategory.getName());
                } else if (recipe.souldata != null) { //don't write null
                    buffer.writeInt(3);
                    buffer.writeUtf(recipe.souldata);
                } else {
                    buffer.writeInt(0);
                }
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing soul binding recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
