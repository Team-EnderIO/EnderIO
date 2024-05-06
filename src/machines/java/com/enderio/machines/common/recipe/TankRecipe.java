package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.EnderRecipe;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// TODO: I'd like to swap the isEmptying for an enum for readability.
public record TankRecipe(
    Ingredient input,
    Item output,
    FluidStack fluid,
    boolean isEmptying
) implements EnderRecipe<TankRecipe.Container> {

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (isEmptying) {
            if (pContainer.getFluidTank().fill(fluid, IFluidHandler.FluidAction.SIMULATE) <= 0) {
                return false;
            }

            return input.test(FluidTankBlockEntity.FLUID_FILL_INPUT.getItemStack(pContainer));
        }

        if (pContainer.getFluidTank().drain(fluid, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
            return false;
        }

        return input.test(FluidTankBlockEntity.FLUID_DRAIN_INPUT.getItemStack(pContainer));
    }

    @Override
    public ItemStack assemble(Container container, HolderLookup.Provider lookupProvider) {
        return null;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return new ItemStack(output);
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

        private final MachineFluidTank fluidTank;

        public Container(IItemHandlerModifiable inv, MachineFluidTank fluidTank) {
            super(inv);
            this.fluidTank = fluidTank;
        }

        public MachineFluidTank getFluidTank() {
            return fluidTank;
        }
    }

    public static class Serializer implements RecipeSerializer<TankRecipe> {

        private static final MapCodec<TankRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(TankRecipe::input),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(TankRecipe::output),
            FluidStack.CODEC.fieldOf("fluid").forGetter(TankRecipe::fluid),
            Codec.BOOL.fieldOf("is_emptying").forGetter(TankRecipe::isEmptying)
        ).apply(instance, TankRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TankRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            TankRecipe::input,
            ByteBufCodecs.registry(Registries.ITEM),
            TankRecipe::output,
            FluidStack.STREAM_CODEC,
            TankRecipe::fluid,
            ByteBufCodecs.BOOL,
            TankRecipe::isEmptying,
            TankRecipe::new
        );

        @Override
        public MapCodec<TankRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TankRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
