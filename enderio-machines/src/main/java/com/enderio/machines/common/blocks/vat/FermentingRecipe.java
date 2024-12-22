package com.enderio.machines.common.blocks.vat;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.datamap.VatReagent;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public record FermentingRecipe(SizedFluidIngredient input, TagKey<Item> leftReagent, TagKey<Item> rightReagent,
        FluidStack output, int ticks) implements MachineRecipe<FermentingRecipe.Input> {

    @Override
    public int getBaseEnergyCost() {
        return 0;
    }

    @Override
    public List<OutputStack> craft(Input input, RegistryAccess registryAccess) {

        double modifier = getModifier(input.getItem(0), leftReagent);
        modifier *= getModifier(input.getItem(1), rightReagent);

        return List.of(OutputStack.of(new FluidStack(output.getFluid(), (int) (output.getAmount() * modifier))));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output().copy()));
    }

    @Override
    public boolean matches(Input input, Level level) {
        FluidStack inputTank = input.getInputTank().getFluid();
        if (!this.input.test(inputTank) || inputTank.getAmount() < this.input.amount()) {
            return false;
        }

        return input.getItem(0).is(leftReagent) && input.getItem(1).is(rightReagent);
    }

    public static double getModifier(ItemStack stack, TagKey<Item> reagent) {
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

    public record Input(ItemStack leftReagent, ItemStack rightStack, MachineFluidTank inputTank)
            implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
            case 0 -> leftReagent;
            case 1 -> rightStack;
            default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public int size() {
            return 2;
        }

        public MachineFluidTank getInputTank() {
            return inputTank;
        }

    }

    public static class Serializer implements RecipeSerializer<FermentingRecipe> {
        private static final StreamCodec<ByteBuf, TagKey<Item>> ITEM_TAG_STREAM_CODEC = ResourceLocation.STREAM_CODEC
                .map(loc -> TagKey.create(Registries.ITEM, loc), TagKey::location);

        public static final MapCodec<FermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SizedFluidIngredient.FLAT_CODEC.fieldOf("input").forGetter(FermentingRecipe::input),
                TagKey.codec(Registries.ITEM).fieldOf("left_reagent").forGetter(FermentingRecipe::leftReagent),
                TagKey.codec(Registries.ITEM).fieldOf("right_reagent").forGetter(FermentingRecipe::rightReagent),
                FluidStack.CODEC.fieldOf("output").forGetter(FermentingRecipe::output),
                Codec.INT.fieldOf("ticks").forGetter(FermentingRecipe::ticks)).apply(instance, FermentingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedFluidIngredient.STREAM_CODEC, FermentingRecipe::input, ITEM_TAG_STREAM_CODEC,
                FermentingRecipe::leftReagent, ITEM_TAG_STREAM_CODEC, FermentingRecipe::rightReagent,
                FluidStack.STREAM_CODEC, FermentingRecipe::output, ByteBufCodecs.INT, FermentingRecipe::ticks,
                FermentingRecipe::new);

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
