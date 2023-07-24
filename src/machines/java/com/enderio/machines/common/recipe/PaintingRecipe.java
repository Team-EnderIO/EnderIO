package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blockentity.PaintingMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaintingRecipe implements MachineRecipe<RecipeWrapper> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final Item output;

    public PaintingRecipe(ResourceLocation id, Ingredient input, Item output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(RecipeWrapper container, Level pLevel) {
        return input.test(PaintingMachineBlockEntity.INPUT.getItemStack(container)) && !PaintingMachineBlockEntity.PAINT.getItemStack(container).isEmpty();
    }

    @Override
    public int getBaseEnergyCost() {
        return MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_ENERGY_COST.get();
    }

    @Override
    public List<OutputStack> craft(RecipeWrapper container, RegistryAccess registryAccess) {
        List<OutputStack> outputs = new ArrayList<>();
        ItemStack outputStack = new ItemStack(output);
        CompoundTag tag = outputStack.getOrCreateTag();
        CompoundTag beTag = new CompoundTag();
        tag.put(BlockItem.BLOCK_ENTITY_TAG, beTag);
        beTag.putString(MachineNBTKeys.PAINT, ForgeRegistries.ITEMS.getKey(PaintingMachineBlockEntity.PAINT.getItemStack(container).getItem()).toString());
        outputs.add(OutputStack.of(outputStack));
        return outputs;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.getDefaultInstance()));
    }

    @Override
    public ItemStack assemble(RecipeWrapper p_44001_, RegistryAccess p_267165_) {
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
        return MachineRecipes.PAINTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.PAINTING.type().get();
    }

    public Ingredient getInput() {
        return input;
    }


    public static class Serializer implements RecipeSerializer<PaintingRecipe> {

        @Override
        public PaintingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            Ingredient input = Ingredient.fromJson(serializedRecipe.get("input").getAsJsonObject());

            ResourceLocation id = new ResourceLocation(serializedRecipe.get("output").getAsString());
            Item output = ForgeRegistries.ITEMS.getValue(id);
            return new PaintingRecipe(recipeId, input, output);
        }

        @Override
        public @Nullable PaintingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                Ingredient input = Ingredient.fromNetwork(buffer);

                ResourceLocation outputId = buffer.readResourceLocation();
                Item output = ForgeRegistries.ITEMS.getValue(outputId);
                if (output == null) {
                    throw new ResourceLocationException("The output of recipe " + recipeId + " does not exist.");
                }

                return new PaintingRecipe(recipeId, input, output);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading painting recipe from packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PaintingRecipe recipe) {
            try {
                recipe.input.toNetwork(buffer);
                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.output)));
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing painting recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
