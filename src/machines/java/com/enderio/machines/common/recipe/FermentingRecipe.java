package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.FluidIngredient;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.datamap.VatReagent;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FermentingRecipe implements MachineRecipe<FermentingRecipe.Container> {

    private final FluidIngredient inputFluid;
    private final int inputFluidAmount;
    private final TagKey<Item> leftReagent;
    private final TagKey<Item> rightReagent;
    private final double outputModifier;
    private final Fluid outputFluid;
    private final int ticks;

    public FermentingRecipe(FluidIngredient inputFluid, int inputFluidAmount, TagKey<Item> leftReagent, TagKey<Item> rightReagent, Fluid outputFluid,
        double outputModifier, int ticks) {
        this.inputFluid = inputFluid;
        this.inputFluidAmount = inputFluidAmount;
        this.leftReagent = leftReagent;
        this.rightReagent = rightReagent;
        this.outputModifier = outputModifier;
        this.outputFluid = outputFluid;
        this.ticks = ticks;
    }

    @Override
    public int getBaseEnergyCost() {
        return 0;
    }

    @Override
    public List<OutputStack> craft(Container container, RegistryAccess registryAccess) {

        double totalModifier = outputModifier;
        totalModifier *= getReagentModifier(container.getItem(0), leftReagent);
        totalModifier *= getReagentModifier(container.getItem(1), rightReagent);

        return List.of(OutputStack.of(new FluidStack(outputFluid, (int) (inputFluidAmount * totalModifier))));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.EMPTY);
    }

    @Override
    public boolean matches(Container container, Level level) {
        FluidStack inputTank = container.getInputTank().getFluid();
        if (!inputFluid.test(inputTank.getFluid()) || inputTank.getAmount() < inputFluidAmount) {
            return false;
        }
        if (!container.getItem(0).is(leftReagent)) {
            return false;
        }
        if (!container.getItem(1).is(rightReagent)) {
            return false;
        }

        return true;
    }

    public double getReagentModifier(ItemStack stack, TagKey<Item> reagent) {
        var map = stack.getItemHolder().getData(VatReagent.DATA_MAP);
        if (map != null) {
            return map.getOrDefault(reagent, 1D);
        }
        return 1;
    }

    public FluidIngredient getInputFluid() {
        return inputFluid;
    }

    public int getInputFluidAmount() {
        return inputFluidAmount;
    }

    public TagKey<Item> getLeftReagent() {
        return leftReagent;
    }

    public TagKey<Item> getRightReagent() {
        return rightReagent;
    }

    public double getOutputModifier() {
        return outputModifier;
    }

    public Fluid getOutputFluid() {
        return outputFluid;
    }

    public int getTicks() {
        return ticks;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.VAT_FERMENTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.VAT_FERMENTING.type().get();
    }

    public static class Container extends RecipeWrapper {

        private final MachineFluidTank inputTank;

        public Container(IItemHandlerModifiable inv, MachineFluidTank inputTank) {
            super(inv);
            this.inputTank = inputTank;
        }

        public MachineFluidTank getInputTank() {
            return inputTank;
        }

    }

    public static class Serializer implements RecipeSerializer<FermentingRecipe> {

        Codec<FermentingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(FermentingRecipe::getInputFluid),
                Codec.INT.fieldOf("input_amount").forGetter(FermentingRecipe::getInputFluidAmount),
                TagKey.codec(Registries.ITEM).fieldOf("left_reagent").forGetter(FermentingRecipe::getLeftReagent),
                TagKey.codec(Registries.ITEM).fieldOf("right_reagent").forGetter(FermentingRecipe::getRightReagent),
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("output_fluid").forGetter(FermentingRecipe::getOutputFluid),
                Codec.DOUBLE.fieldOf("output_modifier").forGetter(FermentingRecipe::getOutputModifier),
                Codec.INT.fieldOf("ticks").forGetter(FermentingRecipe::getTicks))
            .apply(instance, FermentingRecipe::new));

        @Override
        public Codec<FermentingRecipe> codec() {
            return CODEC;
        }

        @Override
        @Nullable
        public FermentingRecipe fromNetwork(FriendlyByteBuf buffer) {
            try {
                FluidIngredient inputFluid = FluidIngredient.fromNetwork(buffer);
                int inputFluidAmount = buffer.readInt();
                TagKey<Item> left = ItemTags.create(buffer.readResourceLocation());
                TagKey<Item> right = ItemTags.create(buffer.readResourceLocation());
                Fluid fluid = Objects.requireNonNull(buffer.readById(BuiltInRegistries.FLUID));
                double outputModifier = buffer.readDouble();
                int ticks = buffer.readInt();

                return new FermentingRecipe(inputFluid, inputFluidAmount, left, right, fluid, outputModifier, ticks);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading fermenting recipe to packet.", ex);
                return null;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FermentingRecipe recipe) {
            try {
                recipe.inputFluid.toNetwork(buffer);
                buffer.writeInt(recipe.inputFluidAmount);
                buffer.writeResourceLocation(recipe.leftReagent.location());
                buffer.writeResourceLocation(recipe.rightReagent.location());
                buffer.writeId(BuiltInRegistries.FLUID, recipe.outputFluid);
                buffer.writeDouble(recipe.outputModifier);
                buffer.writeInt(recipe.ticks);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing fermenting recipe to packet.", ex);
                throw ex;
            }

        }
    }
}