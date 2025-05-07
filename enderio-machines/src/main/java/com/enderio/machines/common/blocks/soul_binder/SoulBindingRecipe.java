package com.enderio.machines.common.blocks.soul_binder;

import com.enderio.base.api.network.MassiveStreamCodec;
import com.enderio.base.api.soul.binding.ingredients.FilledSoulStorageIngredient;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.recipe.FluidRecipeInput;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.souldata.SoulDataReloadListener;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
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

        var vialSoulBindable = vial.getCapability(EIOCapabilities.SoulBindable.ITEM);
        var resultSoulBinding = result.getCapability(EIOCapabilities.SoulBindable.ITEM);

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
    public boolean matches(Input recipeInput, Level pLevel) {
        if (!recipeInput.getItem(0).is(EIOItems.SOUL_VIAL.get())) {
            return false;
        }

        if (!input.test(recipeInput.getItem(1))) {
            return false;
        }

        var soulBindable = recipeInput.getItem(0).getCapability(EIOCapabilities.SoulBindable.ITEM);
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
        return MachineRecipes.SOUL_BINDING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.SOUL_BINDING.type().get();
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

        private static final MapCodec<SoulBindingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(ItemStack.CODEC.fieldOf("output").forGetter(SoulBindingRecipe::output),
                        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SoulBindingRecipe::input),
                        Codec.INT.fieldOf("energy").forGetter(SoulBindingRecipe::energy),
                        Codec.INT.fieldOf("experience").forGetter(SoulBindingRecipe::experience),
                        ResourceLocation.CODEC.optionalFieldOf("entity_type").forGetter(SoulBindingRecipe::entityType),
                        MobCategory.CODEC.optionalFieldOf("mob_category").forGetter(SoulBindingRecipe::mobCategory),
                        Codec.STRING.optionalFieldOf("soul_data").forGetter(SoulBindingRecipe::soulData),
                        Codec.BOOL.optionalFieldOf("copyInputComponents", false)
                                .forGetter(SoulBindingRecipe::copyInputComponents))
                .apply(instance, SoulBindingRecipe::new));

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
