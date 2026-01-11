package com.enderio.enderio.content.machines.soul_binder;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.network.MassiveStreamCodec;
import com.enderio.enderio.api.soul.binding.ingredients.FilledSoulStorageIngredient;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.recipe.FluidRecipeInput;
import com.enderio.enderio.foundation.souldata.SoulDataReloadListener;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record SoulBindingRecipe(ItemStack output, Ingredient input, int energy, int experience,
        Optional<ResourceLocation> entityType, Optional<MobCategory> mobCategory, Optional<String> soulData,
        boolean copyInputComponents) implements MachineRecipe<SoulBindingRecipe.Input> {

    public Ingredient getInput() {
        return input;
    }

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public List<OutputStack> craft(Input input, RegistryAccess registryAccess) {
        ItemStack vial = input.getItem(0);

        List<OutputStack> results = getResultStacks(registryAccess);
        ItemStack result = results.getFirst().getItem();

        if (copyInputComponents) {
            result.applyComponents(input.itemToBind.getComponents());
        }

        var vialSoulBindable = vial.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        var resultSoulBinding = result.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);

        if (vialSoulBindable != null && resultSoulBinding != null) {
            resultSoulBinding.bindSoul(vialSoulBindable.getBoundSoul());
        }

        return results;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.copy()), OutputStack.of(EIOItems.SOUL_VIAL.get().getDefaultInstance()));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, FilledSoulStorageIngredient.of(EIOItems.SOUL_VIAL), input);
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        if (!recipeInput.getItem(0).is(EIOItems.SOUL_VIAL.get())) {
            return false;
        }

        if (!input.test(recipeInput.getItem(1))) {
            return false;
        }

        var soulBindable = recipeInput.getItem(0).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable == null || !soulBindable.hasSoul()) {
            return false;
        }

        var soul = soulBindable.getBoundSoul();
        if (soul.isEmpty()) {
            return false;
        }

        var entityType = Objects.requireNonNull(soul.entityType());

        if (soulData.isPresent()) { // is in the selected souldata
            if (SoulDataReloadListener.fromString(soulData.get())
                    .matches(soul.entityType())
                    .isEmpty()) {
                return false;
            }

            return ExperienceUtil.getLevelFromFluid(recipeInput.getFluid(2).getAmount()) >= experience;
        }

        if (mobCategory.isPresent()) {
            if (!entityType.getCategory().equals(mobCategory.get())) {
                return false;
            }
        }

        if (this.entityType.isPresent()) {
            var entityTypeId = soul.entityTypeId();
            if (!Objects.requireNonNull(entityTypeId).equals(this.entityType.get())) {
                return false;
            }
        }

        return ExperienceUtil.getLevelFromFluid(recipeInput.getFluid(2).getAmount()) >= experience;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EIORecipes.SOUL_BINDING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return EIORecipes.SOUL_BINDING.type().get();
    }

    public record Input(ItemStack boundSoulItem, ItemStack itemToBind, FluidStack experience)
            implements FluidRecipeInput {
        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
            case 0 -> boundSoulItem;
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

    public static class Serializer implements RecipeSerializer<SoulBindingRecipe> {

        private static final MapCodec<SoulBindingRecipe> CODEC = RecordCodecBuilder.<SoulBindingRecipe>mapCodec(instance -> instance
                .group(ItemStack.CODEC.fieldOf("output").forGetter(SoulBindingRecipe::output),
                        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SoulBindingRecipe::input),
                        Codec.INT.fieldOf("energy").forGetter(SoulBindingRecipe::energy),
                        Codec.INT.fieldOf("experience").forGetter(SoulBindingRecipe::experience),
                        ResourceLocation.CODEC.optionalFieldOf("entity_type").forGetter(SoulBindingRecipe::entityType),
                        MobCategory.CODEC.optionalFieldOf("mob_category").forGetter(SoulBindingRecipe::mobCategory),
                        Codec.STRING.optionalFieldOf("soul_data").forGetter(SoulBindingRecipe::soulData),
                        Codec.BOOL.optionalFieldOf("copyInputComponents", false)
                                .forGetter(SoulBindingRecipe::copyInputComponents))
                .apply(instance, SoulBindingRecipe::new))
                .validate(recipe -> {
                    int entityType = recipe.entityType().isPresent() ? 1 : 0;
                    int mobCategory = recipe.mobCategory().isPresent() ? 1 : 0;
                    int soulData = recipe.soulData().isPresent() ? 1 : 0;
                    if (entityType + mobCategory + soulData > 1) {
                        return DataResult.error(() -> "Soul Binding recipe properties entity_type, mob_category and soul_data are mutually exclusive.");
                    }
                    return DataResult.success(recipe);
                });

        public static final StreamCodec<RegistryFriendlyByteBuf, SoulBindingRecipe> STREAM_CODEC = MassiveStreamCodec
                .composite(ItemStack.STREAM_CODEC, SoulBindingRecipe::output, Ingredient.CONTENTS_STREAM_CODEC,
                        SoulBindingRecipe::input, ByteBufCodecs.INT, SoulBindingRecipe::energy, ByteBufCodecs.INT,
                        SoulBindingRecipe::experience, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional),
                        SoulBindingRecipe::entityType,
                        // TODO: 1.21: This is a very gross, could do better.
                        ByteBufCodecs.STRING_UTF8.map(
                                name -> ((StringRepresentable.EnumCodec<MobCategory>) MobCategory.CODEC).byName(name),
                                MobCategory::getName).apply(ByteBufCodecs::optional),
                        SoulBindingRecipe::mobCategory, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional),
                        SoulBindingRecipe::soulData, ByteBufCodecs.BOOL, SoulBindingRecipe::copyInputComponents,
                        SoulBindingRecipe::new);

        @Override
        public MapCodec<SoulBindingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoulBindingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
