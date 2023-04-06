package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.VatBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class VatRecipe implements MachineRecipe<VatBlockEntity.VatContainer>{

    private final ResourceLocation id;
    //Lists the following pairs as valid inputs for the left slot.
    private final List<VatInputPair> leftInputs;
    private final List<VatInputPair> rightInputs;
    private final Fluid inputFluid;
    private final Fluid outputFluid;
    private final float baseConversionRate;
    private final int energy;

    public VatRecipe(ResourceLocation id, List<VatInputPair> possibleLeftInputs, List<VatInputPair> possibleRightInputs, Fluid inputFluid, Fluid outputFluid, float baseConversionRate, int energy) {
        this.id = id;
        this.leftInputs = possibleLeftInputs;
        this.rightInputs = possibleRightInputs;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.baseConversionRate = baseConversionRate;
        this.energy = energy;
    }

    @Override
    public int getEnergyCost(VatBlockEntity.VatContainer container) {
        return energy;
    }

    @Override
    public List<OutputStack> craft(VatBlockEntity.VatContainer container) {
        int recipeResultAmount = calcFluidProductionForItemCombo(
            VatBlockEntity.LEFT_SLOT.getItemStack(container).getItem(),
            VatBlockEntity.RIGHT_SLOT.getItemStack(container).getItem());

        FluidStack stack = new FluidStack(outputFluid, recipeResultAmount);
        return List.of(OutputStack.of(stack));
    }

    //Functionality is not used
    @Override
    public List<OutputStack> getResultStacks() {
        return List.of(OutputStack.EMPTY);
    }

    @Override
    public boolean matches(VatBlockEntity.VatContainer container, Level level) {
        ItemStack leftSlotItemStack = VatBlockEntity.LEFT_SLOT.getItemStack(container);
        ItemStack rightSlotItemStack = VatBlockEntity.RIGHT_SLOT.getItemStack(container);
        boolean leftMatch = false;
        for (VatInputPair pair: leftInputs) {
            if(pair.ingredient.test(leftSlotItemStack)){
                leftMatch = true;
                break;
            }
        }

        boolean rightMatch = false;
        for (VatInputPair pair: rightInputs) {
            if(pair.ingredient.test(rightSlotItemStack)){
                rightMatch = true;
                break;
            }
        }

        return leftMatch & rightMatch;
    }

    /**
     * Tests whether an item stack is a valid input for this recipe in the left slot.
     * @param item The item stack that is to be tested.
     * @return Whether the item stack matches.
     */
    public boolean matchesLeft(ItemStack item) {
        return leftInputs.stream().anyMatch(vatInputPair -> vatInputPair.ingredient.test(item));
    }

    /**
     * Tests whether an item stack is a valid input for this recipe in the right slot.
     * @param item The item stack that is to be tested.
     * @return Whether the item stack matches.
     */
    public boolean matchesRight(ItemStack item) {
        return rightInputs.stream().anyMatch(vatInputPair -> vatInputPair.ingredient.test(item));
    }

    /**
     * Calculates the amount of input fluid that is consumed for an item combo with this recipe
     * @param left The item that is put in the left input slot
     * @param right The item that is put in the right input slot
     * @return the amount of millibuckets of fluid to produce
     */
    public int calcFluidConsumptionForItemCombo(Item left, Item right){
        Optional<VatInputPair> leftPair = leftInputs.stream().filter(vatInputPair -> vatInputPair.ingredient().test(new ItemStack(left, 1))).findAny();
        Optional<VatInputPair> rightPair = rightInputs.stream().filter(vatInputPair -> vatInputPair.ingredient().test(new ItemStack(right, 1))).findAny();
        assert leftPair.isPresent() && rightPair.isPresent():"No recipe match";
        return calcFluidConsumption(leftPair.get().multiplier, rightPair.get().multiplier);
    }

    /**
     * Calculates the amount of output fluid that is produced for an item combo with this recipe
     * @param left The item that is put in the left input slot
     * @param right The item that is put in the right input slot
     * @return the amount of millibuckets of fluid to produce
     */
    public int calcFluidProductionForItemCombo(Item left, Item right){
        Optional<VatInputPair> leftPair = leftInputs.stream().filter(vatInputPair -> vatInputPair.ingredient().test(new ItemStack(left, 1))).findAny();
        Optional<VatInputPair> rightPair = rightInputs.stream().filter(vatInputPair -> vatInputPair.ingredient().test(new ItemStack(right, 1))).findAny();
        assert leftPair.isPresent() && rightPair.isPresent():"No recipe match";
        return calcFluidProduction(leftPair.get().multiplier, rightPair.get().multiplier);
    }

    /**
     * Function for performing the maths for controlling how much output fluid the vat produces for given float modifiers.
     * @param leftModifier Float representation of the modifier associated with the left input item.
     * @param rightModifier Float representation of the modifier associated with the right input item.
     * @return The amount of millibuckets that is produced
     */
    public int calcFluidProduction(float leftModifier, float rightModifier){
        return (int) Math.floor(1000*leftModifier*rightModifier*baseConversionRate);
    }

    /**
     * Function for performing the maths for controlling how much fluid the vat comsumes for given float modifiers.
     * @param leftModifier Float representation of the modifier associated with the left input item.
     * @param rightModifier Float representation of the modifier associated with the right input item.
     * @return The amount of millibuckets that is consumed
     */
    public int calcFluidConsumption(float leftModifier, float rightModifier){
        return (int) Math.floor(1000*leftModifier*rightModifier);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.VATTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.VATTING.type().get();
    }
    public Fluid getOutputFluid() {
        return outputFluid;
    }
    public Fluid getInputFluid() {
        return inputFluid;
    }
    public List<Ingredient> getLeftInputItems() {
        return leftInputs.stream().map(vatInputPair -> vatInputPair.ingredient).collect(Collectors.toList());
    }
    public List<Ingredient> getRightInputItems() {
        return rightInputs.stream().map(vatInputPair -> vatInputPair.ingredient).collect(Collectors.toList());
    }
    public float getMultiplierForLeftInputItem(ItemStack item){
        Optional<VatInputPair> op = leftInputs.stream().filter(vatInputPair -> vatInputPair.ingredient.test(item)).findAny();
        assert op.isPresent();
        return op.get().multiplier;
    }
    public float getMultiplierForRightInputItem(ItemStack item){
        Optional<VatInputPair> op = rightInputs.stream().filter(vatInputPair -> vatInputPair.ingredient.test(item)).findAny();
        assert op.isPresent();
        return op.get().multiplier;
    }
    public record VatInputPair(Ingredient ingredient,float multiplier){}

    public static class Serializer implements RecipeSerializer<VatRecipe> {

        @Override
        public VatRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            // Load left item ingredients
            JsonArray jsonLeftIngredients = serializedRecipe.getAsJsonArray("leftInputs");
            JsonArray jsonLeftMultipliers = serializedRecipe.getAsJsonArray("leftMultipliers");
            List<VatInputPair> possibleLeftInputs = new ArrayList<>();
            for (int i = 0; i < jsonLeftIngredients.size(); i++) {
                possibleLeftInputs.add(new VatInputPair(Ingredient.fromJson(jsonLeftIngredients.get(i)), jsonLeftMultipliers.get(i).getAsFloat()));
            }

            // Load right item ingredients
            JsonArray jsonRightIngredients = serializedRecipe.getAsJsonArray("rightInputs");
            JsonArray jsonRightMultipliers = serializedRecipe.getAsJsonArray("rightMultipliers");
            List<VatInputPair> possibleRightInputs = new ArrayList<>();
            for (int i = 0; i < jsonRightIngredients.size(); i++) {
                possibleRightInputs.add(new VatInputPair(Ingredient.fromJson(jsonRightIngredients.get(i)), jsonRightMultipliers.get(i).getAsFloat()));
            }
            // Load fluids
            String inputFluidResourceLocationName = serializedRecipe.get("inputFluid").getAsString();
            Fluid inputFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(inputFluidResourceLocationName));
            assert inputFluid != null : " Couldn't find fluid for id \"" + inputFluidResourceLocationName + "\", No such fluid?";
            String outputFluidResourceLocationName = serializedRecipe.get("outputFluid").getAsString();
            Fluid outputFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(outputFluidResourceLocationName));
            assert outputFluid != null : " Couldn't find fluid for id \"" + outputFluidResourceLocationName + "\", No such fluid?";
            float baseConversionRate = serializedRecipe.get("baseConversionRate").getAsFloat();
            // Load energy
            int energy = serializedRecipe.get("energy").getAsInt();


            return new VatRecipe(recipeId, possibleLeftInputs, possibleRightInputs, inputFluid, outputFluid, baseConversionRate, energy);
        }

        @Nullable
        @Override
        public VatRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                int leftInputListLength = buffer.readInt();
                List<VatInputPair> leftInputPairs = new ArrayList<>();
                for (int i = 0; i < leftInputListLength; i++) {
                    leftInputPairs.add(new VatInputPair(Ingredient.fromNetwork(buffer), buffer.readFloat()));
                }

                int rightInputListLength = buffer.readInt();
                List<VatInputPair> rightInputPairs = new ArrayList<>();
                for (int i = 0; i < rightInputListLength; i++) {
                    rightInputPairs.add(new VatInputPair(Ingredient.fromNetwork(buffer), buffer.readFloat()));
                }

                Fluid inputFluid = ForgeRegistries.FLUIDS.getValue(buffer.readResourceLocation());
                Fluid outputFluid = ForgeRegistries.FLUIDS.getValue(buffer.readResourceLocation());
                float baseConversionRate = buffer.readFloat();
                int energy = buffer.readInt();

                assert inputFluid != null;
                assert outputFluid != null;
                return new VatRecipe(recipeId, leftInputPairs, rightInputPairs, inputFluid, outputFluid, baseConversionRate, energy);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading vat smelting recipe to packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, VatRecipe recipe) {
            try {
                buffer.writeInt(recipe.leftInputs.size());
                for (VatInputPair pair : recipe.leftInputs) {
                    pair.ingredient.toNetwork(buffer);
                    buffer.writeFloat(pair.multiplier);
                }

                buffer.writeInt(recipe.rightInputs.size());
                for (VatInputPair pair : recipe.rightInputs) {
                    pair.ingredient.toNetwork(buffer);
                    buffer.writeFloat(pair.multiplier);
                }

                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(recipe.inputFluid)));
                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(recipe.outputFluid)));
                buffer.writeFloat(recipe.baseConversionRate);
                buffer.writeInt(recipe.energy);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing allow smelting recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
