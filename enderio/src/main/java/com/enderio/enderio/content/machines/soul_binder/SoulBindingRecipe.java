package com.enderio.enderio.content.machines.soul_binder;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.api.soul.binding.display.SoulBoundSlotDisplay;
import com.enderio.enderio.api.soul.binding.ingredients.AnyBindableSoulSourceIngredient;
import com.enderio.enderio.api.soul.ingredient.SoulIngredient;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.recipe.FluidRecipeInput;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.foundation.util.EntityCaptureUtils;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

public final class SoulBindingRecipe implements MachineRecipe<SoulBindingRecipe.Input> {

    // @formatter:off

    private static final MapCodec<SoulBindingRecipe> MAP_CODEC = RecordCodecBuilder
        .<SoulBindingRecipe>mapCodec(instance -> instance
            .group(ItemStackTemplate.CODEC.optionalFieldOf("output").forGetter(recipe -> Optional.ofNullable(recipe.output())),
                OutputMode.CODEC.fieldOf("output_mode").forGetter(SoulBindingRecipe::outputMode),
                Ingredient.CODEC.fieldOf("input").forGetter(SoulBindingRecipe::input),
                Ingredient.CODEC.optionalFieldOf("soul_source", AnyBindableSoulSourceIngredient.INSTANCE.toVanilla()).forGetter(SoulBindingRecipe::soulSource),
                SoulIngredient.CODEC.optionalFieldOf("input_soul", SoulIngredient.any()).forGetter(SoulBindingRecipe::inputSoul),
                Codec.INT.fieldOf("energy").forGetter(SoulBindingRecipe::energy), Codec.INT.fieldOf("experience").forGetter(SoulBindingRecipe::experience),
                Codec.BOOL.optionalFieldOf("copy_input_components", false).forGetter(SoulBindingRecipe::copyInputComponents))
            .apply(instance, SoulBindingRecipe::new))
        .validate(recipe -> {
            if (recipe.outputMode.requiresOutput() == (recipe.output == null)) {
                return DataResult.error(() -> "output is required for output_mode=transform or transform_and_bind and forbidden for output_mode=bind");
            }
            return DataResult.success(recipe);
        });

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulBindingRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), recipe -> Optional.ofNullable(recipe.output()),
        OutputMode.STREAM_CODEC, SoulBindingRecipe::outputMode,
        Ingredient.CONTENTS_STREAM_CODEC, SoulBindingRecipe::input,
        Ingredient.CONTENTS_STREAM_CODEC, SoulBindingRecipe::soulSource,
        SoulIngredient.STREAM_CODEC, SoulBindingRecipe::inputSoul,
        ByteBufCodecs.INT, SoulBindingRecipe::energy,
        ByteBufCodecs.INT, SoulBindingRecipe::experience,
        ByteBufCodecs.BOOL, SoulBindingRecipe::copyInputComponents,
        SoulBindingRecipe::new);

    // @formatter:on

    public static final RecipeSerializer<SoulBindingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Nullable
    private final ItemStackTemplate output;
    private final OutputMode outputMode;
    private final Ingredient input;
    private final Ingredient soulSource;
    private final SoulIngredient inputSoul;
    private final int energy;
    private final int experience;
    private final boolean copyInputComponents;

    @Nullable
    private PlacementInfo placementInfo;

    public SoulBindingRecipe(Optional<ItemStackTemplate> output, OutputMode outputMode, Ingredient input, Ingredient soulSource, SoulIngredient inputSoul, int energy, int experience,
        boolean copyInputComponents) {
        this.output = output.orElse(null);
        this.outputMode = outputMode;
        this.input = input;
        this.soulSource = soulSource;
        this.inputSoul = inputSoul;
        this.energy = energy;
        this.experience = experience;
        this.copyInputComponents = copyInputComponents;
    }

    @Nullable
    public ItemStackTemplate output() {
        return output;
    }

    public OutputMode outputMode() {
        return outputMode;
    }

    public Ingredient input() {
        return input;
    }

    public Ingredient soulSource() {
        return soulSource;
    }

    public SoulIngredient inputSoul() {
        return inputSoul;
    }

    public int energy() {
        return energy;
    }

    public int experience() {
        return experience;
    }

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public List<OutputStack> craft(Input input, RegistryAccess registryAccess) {
        var extracted = resolveSoulAndRemainder(input.soulSourceItem).orElseThrow(
            () -> new IllegalArgumentException("Soul binding recipe input has no extractable soul"));
        var result = resolveOutputStack(input.itemToBind, extracted.soul()).orElseThrow(
            () -> new IllegalStateException("Soul binding recipe cannot bind the supplied soul"));
        return List.of(OutputStack.of(result), OutputStack.of(extracted.remainder()));
    }

    /**
     * Extracts one soul on a detached source item, retaining the item left by its capability.
     */
    // TODO: Consider a soul binding utility rather than having it in the recipe.
    public static Optional<ExtractedSoulSource> resolveSoulAndRemainder(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        // ItemAccess.forStack won't allow the item to change, so we need a temporary resource handler.
        var handler = new ItemStacksResourceHandler(NonNullList.of(ItemStack.EMPTY, stack.copyWithCount(1)));
        var access = ItemAccess.forHandlerIndexStrict(handler, 0);
        var source = access.getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE);
        if (source == null) {
            return Optional.empty();
        }

        // Extract the soul and transform the item into its remainder.
        try (Transaction transaction = Transaction.openRoot()) {
            Soul soul = source.extractSoul(transaction);
            if (soul.isEmpty()) {
                return Optional.empty();
            }

            transaction.commit();
            return Optional.of(new ExtractedSoulSource(soul, access.getResource().toStack(access.getAmount())));
        }
    }

    public record ExtractedSoulSource(Soul soul, ItemStack remainder) {}

    /**
     * Resolve the output stack for the given item and soul.
     *
     * @param itemToBind input item to be bound.
     * @param soul the soul to bind.
     * @return the resulting stack, if any.
     */
    public Optional<ItemStack> resolveOutputStack(ItemStack itemToBind, Soul soul) {
        if (itemToBind.isEmpty() || soul.isEmpty()) {
            return Optional.empty();
        }

        ItemStack seed = outputMode.requiresOutput() ? Objects.requireNonNull(output).create() : itemToBind.copyWithCount(1);
        if (outputMode.requiresOutput() && copyInputComponents) {
            seed.applyComponents(itemToBind.getComponents());
        }

        if (!outputMode.bindsSoul()) {
            return Optional.of(seed);
        }

        return SoulBoundUtils.tryBind(seed, soul);
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        // Not used by consumers of SoulBindingRecipe anymore, and I plan to remove this.
        throw new NotImplementedException();
    }

    private List<ItemStack> getBindingPreviews() {
        var results = new ArrayList<ItemStack>();
        for (var item : input.items().toList()) {
            for (var entity : inputSoul.getEntityTypes()) {
                if (EntityCaptureUtils.getCapturableStatus(entity.value()) != EntityCaptureUtils.CapturableStatus.CAPTURABLE) {
                    continue;
                }

                resolveOutputStack(item.value().getDefaultInstance(), Soul.of(entity.value())).ifPresent(results::add);
            }
        }
        return results;
    }

    @Override
    public List<RecipeDisplay> display() {
        SlotDisplay result;
        if (outputMode().requiresOutput()) {
            result = new SlotDisplay.ItemStackSlotDisplay(Objects.requireNonNull(output));
        } else {
            result = input().display();
        }

        if (outputMode().bindsSoul()) {
            HolderSet<EntityType<?>> inputEntityTypes = HolderSet.direct(inputSoul.getEntityTypes());
            result = new SoulBoundSlotDisplay(inputEntityTypes, result);
        }

        // TODO: Need to show the soul source with the right binding on it...
        // TODO: Do we allow the slot displays to de-sync, or do we return one display for each potential soul?
        return List.of(new SoulBindingDisplay(soulSource.display(), input.display(), result, new SlotDisplay.ItemSlotDisplay(EIOBlocks.SOUL_BINDER.asItem())));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.SOUL_BINDING.get();
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        if (!soulSource.test(recipeInput.getItem(0))) {
            return false;
        }

        if (!input.test(recipeInput.getItem(1))) {
            return false;
        }

        var soulBindable = ItemAccess.forStack(recipeInput.getItem(0)).getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE);
        if (soulBindable == null || !soulBindable.isSoulAvailable()) {
            return false;
        }

        var bindableSoul = soulBindable.availableSoul();
        if (bindableSoul.isEmpty() || !inputSoul.test(bindableSoul)) {
            return false;
        }

        return ExperienceUtil.getLevelFromFluid(recipeInput.getFluid(2).getAmount()) >= experience && resolveOutputStack(recipeInput.itemToBind,
            bindableSoul).isPresent();
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipeTypes.SOUL_BINDING.get();
    }

    public boolean copyInputComponents() {
        return copyInputComponents;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(input);
        }

        return placementInfo;
    }

    public enum OutputMode implements StringRepresentable {
        /**
         * Binds the input soul onto the input item, without transforming it.
         */
        BIND(0, "bind", false, true),

        /**
         * Transforms the input item and soul into a new item.
         * Requires a supplied output item, but does not attempt to bind a soul to the transformation result.
         */
        TRANSFORM(1, "transform", true, false),

        /**
         * Transforms the input and binds the soul onto the result.
         * Requires an output to be supplied to the recipe, and the output must be soul bindable.
         */
        TRANSFORM_AND_BIND(2, "transform_and_bind", true, true);

        public static final Codec<OutputMode> CODEC = StringRepresentable.fromEnum(OutputMode::values);
        public static final IntFunction<OutputMode> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, OutputMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

        private final int id;
        private final String name;
        private final boolean requiresOutput;
        private final boolean bindsSoul;

        OutputMode(int id, String name, boolean requiresOutput, boolean bindsSoul) {
            this.id = id;
            this.name = name;
            this.requiresOutput = requiresOutput;
            this.bindsSoul = bindsSoul;
        }

        public boolean requiresOutput() {
            return requiresOutput;
        }

        public boolean bindsSoul() {
            return bindsSoul;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public record Input(ItemStack soulSourceItem, ItemStack itemToBind, FluidStack experience) implements FluidRecipeInput {
        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
                case 0 -> soulSourceItem;
                case 1 -> itemToBind;
                case 2 -> ItemStack.EMPTY;
                default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public FluidStack getFluid(int slotIndex) {
            if (slotIndex != 2) {
                throw new IllegalArgumentException("No fluid for index " + slotIndex);
            }

            return experience;
        }

        @Override
        public int size() {
            return 3;
        }
    }

    // TODO: Include soul source.
    public record SoulBindingDisplay(SlotDisplay soulSource, SlotDisplay input, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        // @formatter:off

        public static final MapCodec<SoulBindingDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(p_379634_ -> p_379634_
            .group(
                SlotDisplay.CODEC.fieldOf("soul_source").forGetter(SoulBindingDisplay::soulSource),
                SlotDisplay.CODEC.fieldOf("input").forGetter(SoulBindingDisplay::input),
                SlotDisplay.CODEC.fieldOf("result").forGetter(SoulBindingDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SoulBindingDisplay::craftingStation))
            .apply(p_379634_, SoulBindingDisplay::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SoulBindingDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC, SoulBindingDisplay::soulSource,
            SlotDisplay.STREAM_CODEC, SoulBindingDisplay::input,
            SlotDisplay.STREAM_CODEC, SoulBindingDisplay::result,
            SlotDisplay.STREAM_CODEC, SoulBindingDisplay::craftingStation,
            SoulBindingDisplay::new);

        // @formatter:on

        public static final Type<SoulBindingDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flagSet) {
            return this.soulSource.isEnabled(flagSet) && this.input.isEnabled(flagSet) && RecipeDisplay.super.isEnabled(flagSet);
        }
    }
}
