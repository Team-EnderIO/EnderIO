package com.enderio.machines.common.blocks.obelisks.weather;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
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
        CLEAR("clear",
                new Fireworks(2,
                        List.of(new FireworkExplosion(FireworkExplosion.Shape.LARGE_BALL,
                                IntList.of(DyeColor.YELLOW.getFireworkColor(), DyeColor.WHITE.getFireworkColor()),
                                IntList.of(DyeColor.WHITE.getFireworkColor()), false, true)))),
        RAIN("rain",
                new Fireworks(2,
                        List.of(new FireworkExplosion(FireworkExplosion.Shape.LARGE_BALL,
                                IntList.of(DyeColor.BLUE.getFireworkColor(), DyeColor.LIGHT_BLUE.getFireworkColor()),
                                IntList.of(DyeColor.LIGHT_BLUE.getFireworkColor()), false, true)))),
        LIGHTNING("lightning",
                new Fireworks(2,
                        List.of(new FireworkExplosion(FireworkExplosion.Shape.LARGE_BALL,
                                IntList.of(DyeColor.BLUE.getFireworkColor(), DyeColor.YELLOW.getFireworkColor()),
                                IntList.of(DyeColor.YELLOW.getFireworkColor()), false, true))));

        public static final Codec<WeatherMode> CODEC = StringRepresentable.fromEnum(WeatherMode::values);
        public static final IntFunction<WeatherMode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(),
                ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, WeatherMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
                Enum::ordinal);
        private final String type;
        private final Fireworks fireworks;
        public static final Fireworks SURPRISE = new Fireworks(2,
                List.of(new FireworkExplosion(FireworkExplosion.Shape.LARGE_BALL,
                        IntList.of(DyeColor.RED.getFireworkColor(), DyeColor.ORANGE.getFireworkColor(),
                                DyeColor.YELLOW.getFireworkColor(), DyeColor.GREEN.getFireworkColor(),
                                DyeColor.BLUE.getFireworkColor(), DyeColor.PURPLE.getFireworkColor()),
                        IntList.of(), true, true)));

        public static final Fireworks SURPRISE_2 = new Fireworks(2,
                List.of(new FireworkExplosion(
                        FireworkExplosion.Shape.LARGE_BALL, IntList.of(DyeColor.LIGHT_BLUE.getFireworkColor(),
                                DyeColor.PINK.getFireworkColor(), DyeColor.WHITE.getFireworkColor()),
                        IntList.of(), true, true)));

        WeatherMode(String type, Fireworks fireworks) {
            this.type = type;
            this.fireworks = fireworks;
        }

        @Override
        public String getSerializedName() {
            return type;
        }

        public Fireworks getFireworks() {
            return fireworks;
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
