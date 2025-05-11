package com.enderio.machines.common.blocks.obelisks.weather;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public record WeatherChangeRecipe(FluidStack fluid, WeatherMode mode)
        implements MachineRecipe<WeatherChangeRecipe.Input> {

    @Override
    public int getBaseEnergyCost() {
        return 0;
    }

    @Override
    public List<OutputStack> craft(Input container, RegistryAccess registryAccess) {
        return List.of();
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of();
    }

    @Override
    public boolean matches(Input input, Level level) {
        return FluidStack.isSameFluid(input.fluid(), fluid) && input.fluid.getAmount() >= fluid.getAmount();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.WEATHER_CHANGE.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.WEATHER_CHANGE.type().get();
    }

    public record Input(FluidStack fluid) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    public enum WeatherMode implements StringRepresentable {
        CLEAR("clear"), RAIN("rain"), LIGHTNING("lightning");

        public static final Codec<WeatherMode> CODEC = StringRepresentable.fromEnum(WeatherMode::values);
        public static final IntFunction<WeatherMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(),
                ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, WeatherMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
                Enum::ordinal);
        private final String type;

        WeatherMode(String type) {
            this.type = type;
        }

        @Override
        public String getSerializedName() {
            return type;
        }
    }

    public static class Serializer implements RecipeSerializer<WeatherChangeRecipe> {
        public static final MapCodec<WeatherChangeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
                .group(FluidStack.CODEC.fieldOf("fluid").forGetter(WeatherChangeRecipe::fluid),
                        WeatherMode.CODEC.fieldOf("mode").forGetter(WeatherChangeRecipe::mode))
                .apply(inst, WeatherChangeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WeatherChangeRecipe> STREAM_CODEC = StreamCodec
                .composite(FluidStack.STREAM_CODEC, WeatherChangeRecipe::fluid, WeatherMode.STREAM_CODEC,
                        WeatherChangeRecipe::mode, WeatherChangeRecipe::new);

        @Override
        public MapCodec<WeatherChangeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WeatherChangeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
