package com.enderio.machines.common.recipe;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.datamap.VatReagent;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.List;

public record FermentingRecipe(SizedFluidIngredient input, TagKey<Item> leftReagent, TagKey<Item> rightReagent, FluidStack output, int ticks)
    implements MachineRecipe<FermentingRecipe.Container> {

    @Override
    public int getBaseEnergyCost() {
        return 0;
    }

    @Override
    public List<OutputStack> craft(Container container, RegistryAccess registryAccess) {

        double totalModifier = 1;
        totalModifier *= getReagentModifier(container.getItem(0), leftReagent);
        totalModifier *= getReagentModifier(container.getItem(1), rightReagent);

        return List.of(OutputStack.of(new FluidStack(output.getFluid(), (int) (output.getAmount() * totalModifier))));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.EMPTY);
    }

    @Override
    public boolean matches(Container container, Level level) {
        FluidStack inputTank = container.getInputTank().getFluid();
        if (!input.test(inputTank) || inputTank.getAmount() < input.amount()) {
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
        private static final StreamCodec<ByteBuf, TagKey<Item>> ItemTagStreamCodec = ResourceLocation.STREAM_CODEC.map(
            loc -> TagKey.create(Registries.ITEM, loc), TagKey::location);

        MapCodec<FermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(SizedFluidIngredient.FLAT_CODEC.fieldOf("input_fluid").forGetter(FermentingRecipe::input),
                TagKey.codec(Registries.ITEM).fieldOf("left_reagent").forGetter(FermentingRecipe::leftReagent),
                TagKey.codec(Registries.ITEM).fieldOf("right_reagent").forGetter(FermentingRecipe::rightReagent),
                FluidStack.CODEC.fieldOf("output_fluid").forGetter(FermentingRecipe::output), Codec.INT.fieldOf("ticks").forGetter(FermentingRecipe::ticks))
            .apply(instance, FermentingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> STREAM_CODEC = StreamCodec.composite(SizedFluidIngredient.STREAM_CODEC,
            FermentingRecipe::input, ItemTagStreamCodec, FermentingRecipe::leftReagent, ItemTagStreamCodec, FermentingRecipe::rightReagent,
            FluidStack.STREAM_CODEC, FermentingRecipe::output, ByteBufCodecs.INT, FermentingRecipe::ticks, FermentingRecipe::new);

        @Override
        public MapCodec<FermentingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
