package com.enderio.machines.common.blocks.sag_mill;

import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.util.TagUtil;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.IntFunction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record SagMillingRecipe(Ingredient input, List<OutputItem> outputs, int energy, BonusType bonusType)
        implements MachineRecipe<SagMillingRecipe.Input> {

    private static final Random RANDOM = new Random();

    /**
     * JEI for sag mill will not use this, it'll use a capacitor data.
     */
    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public int getEnergyCost(Input recipeInput) {
        return getEnergyCost(recipeInput.grindingBallData());
    }

    public int getEnergyCost(GrindingBallData grindingBallData) {
        return (int) (energy * grindingBallData.powerUse());
    }

    @Override
    public List<OutputStack> craft(Input recipeInput, RegistryAccess registryAccess) {
        List<OutputStack> outputs = new ArrayList<>();

        // Iterate over the number of outputs
        float outputCount = bonusType.canMultiply() ? recipeInput.grindingBallData().outputMultiplier() : 1.0f;
        float chanceMult = bonusType.doChance() ? recipeInput.grindingBallData().bonusMultiplier() : 1.0f;

        // Iterate over the number of outputs.
        // Without a grinding ball this only runs once.
        while (outputCount > 0) {
            if (RANDOM.nextFloat() < outputCount) {
                for (OutputItem output : this.outputs) {
                    if (output.isPresent() && RANDOM.nextFloat() < output.chance() * chanceMult) {
                        // Collect the output
                        ItemStack outputStack = output.getItemStack();

                        // Attempt to add to an existing originalStack.
                        for (OutputStack stack : outputs) {
                            if (outputStack.getCount() <= 0) {
                                break;
                            }

                            ItemStack itemStack = stack.getItem();
                            if (itemStack.is(outputStack.getItem())) {
                                int growth = Math.min(outputStack.getCount(), itemStack.getMaxStackSize());
                                itemStack.grow(growth);
                                outputStack.shrink(growth);
                            }
                        }

                        // Add new originalStack.
                        if (outputStack.getCount() >= 0) {
                            outputs.add(OutputStack.of(outputStack));
                        }
                    }
                }
            }
            outputCount--;
        }

        return outputs;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        // TODO: This logic seems dumb.
        // Gather guaranteed outputs (that are loaded)
        List<OutputStack> guaranteedOutputs = new ArrayList<>();
        for (OutputItem item : outputs) {
            if (item.chance >= 1.0f && item.isPresent()) {
                guaranteedOutputs.add(OutputStack.of(item.getItemStack()));
            }
        }
        return guaranteedOutputs;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        return input.test(recipeInput.getItem(0));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.SAG_MILLING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.SAG_MILLING.type().get();
    }

    public enum BonusType implements StringRepresentable {
        NONE(0, false, false), MULTIPLY_OUTPUT(1, true, true), CHANCE_ONLY(2, false, true);

        public static final Codec<BonusType> CODEC = StringRepresentable.fromEnum(BonusType::values);
        public static final IntFunction<BonusType> BY_ID = ByIdMap.continuous(key -> key.id, values(),
                ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, BonusType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

        private final int id;
        private final boolean multiply;
        private final boolean chance;

        BonusType(int id, boolean multiply, boolean chance) {
            this.id = id;
            this.multiply = multiply;
            this.chance = chance;
        }

        public boolean canMultiply() {
            return multiply;
        }

        public boolean doChance() {
            return chance;
        }

        public boolean useGrindingBall() {
            return multiply || chance;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public record OutputItem(Either<ItemStack, SizedTagOutput> output, float chance, boolean isOptional) {

        private static final Codec<OutputItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.either(ItemStack.CODEC, SizedTagOutput.CODEC).fieldOf("item").forGetter(OutputItem::output),
                Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter(OutputItem::chance),
                Codec.BOOL.optionalFieldOf("optional", false).forGetter(OutputItem::isOptional))
                .apply(instance, OutputItem::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, OutputItem> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.either(ItemStack.STREAM_CODEC, SizedTagOutput.STREAM_CODEC), OutputItem::output,
                ByteBufCodecs.FLOAT, OutputItem::chance, ByteBufCodecs.BOOL, OutputItem::isOptional, OutputItem::new);

        public static OutputItem of(Item item, int count, float chance, boolean optional) {
            return of(new ItemStack(item, count), chance, optional);
        }

        public static OutputItem of(ItemStack item, float chance, boolean optional) {
            return new OutputItem(Either.left(item), chance, optional);
        }

        public static OutputItem of(TagKey<Item> tag, int count, float chance, boolean optional) {
            return new OutputItem(Either.right(new SizedTagOutput(tag, count)), chance, optional);
        }

        public boolean isPresent() {
            return !getItemStack().isEmpty();
        }

        public ItemStack getItemStack() {
            return output.map(ItemStack::copy, SizedTagOutput::getItemStack);
        }

        public record SizedTagOutput(TagKey<Item> itemTag, int count) {
            private static final Codec<SizedTagOutput> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(SizedTagOutput::itemTag),
                            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(SizedTagOutput::count))
                    .apply(instance, SizedTagOutput::new));

            private static final StreamCodec<RegistryFriendlyByteBuf, SizedTagOutput> STREAM_CODEC = StreamCodec
                    .composite(
                            ResourceLocation.STREAM_CODEC.map(loc -> TagKey.create(Registries.ITEM, loc),
                                    TagKey::location),
                            SizedTagOutput::itemTag, ByteBufCodecs.INT, SizedTagOutput::count, SizedTagOutput::new);

            public ItemStack getItemStack() {
                return TagUtil.getOptionalItem(itemTag).map(item -> new ItemStack(item, count)).orElse(ItemStack.EMPTY);
            }
        }
    }

    public record Input(ItemStack inputItemStack, GrindingBallData grindingBallData) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            if (slotIndex != 0) {
                throw new IllegalArgumentException("No item for index " + slotIndex);
            }

            return inputItemStack;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    public static class Serializer implements RecipeSerializer<SagMillingRecipe> {

        public static final MapCodec<SagMillingRecipe> CODEC = RecordCodecBuilder
                .mapCodec(instance -> instance
                        .group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SagMillingRecipe::input),
                                OutputItem.CODEC.listOf().fieldOf("outputs").forGetter(SagMillingRecipe::outputs),
                                Codec.INT.fieldOf("energy").forGetter(SagMillingRecipe::energy),
                                BonusType.CODEC.optionalFieldOf("bonus", BonusType.MULTIPLY_OUTPUT)
                                        .forGetter(SagMillingRecipe::bonusType))
                        .apply(instance, SagMillingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SagMillingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SagMillingRecipe::input,
                OutputItem.STREAM_CODEC.apply(ByteBufCodecs.list()), SagMillingRecipe::outputs, ByteBufCodecs.INT,
                SagMillingRecipe::energy, BonusType.STREAM_CODEC, SagMillingRecipe::bonusType, SagMillingRecipe::new);

        @Override
        public MapCodec<SagMillingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SagMillingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
