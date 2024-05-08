package com.enderio.machines.common.recipe;

import com.enderio.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.SoulBinderBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.souldata.SoulDataReloadListener;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public record SoulBindingRecipe(
    Item output,
    Ingredient input,
    int energy,
    int experience,
    Optional<ResourceLocation> entityType,
    Optional<MobCategory> mobCategory,
    Optional<String> soulData
) implements MachineRecipe<SoulBindingRecipe.Container> {

    public SoulBindingRecipe(Item output, Ingredient input, int energy, int exp, ResourceLocation entityType) {
        this(output, input, energy, exp, Optional.of(entityType), Optional.empty(), Optional.empty());
    }

    public SoulBindingRecipe(Item output, Ingredient input, int energy, int exp, MobCategory mobCategory) {
        this(output, input, energy, exp, Optional.empty(), Optional.of(mobCategory), Optional.empty());
    }

    public SoulBindingRecipe(Item output, Ingredient input, int energy, int exp, String souldata) {
        this(output, input, energy, exp, Optional.empty(), Optional.empty(), Optional.of(souldata));
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public List<OutputStack> craft(SoulBindingRecipe.Container container, RegistryAccess registryAccess) {
        ItemStack vial = SoulBinderBlockEntity.INPUT_SOUL.getItemStack(container);
        List<OutputStack> results = getResultStacks(registryAccess);
        ItemStack result = results.get(0).getItem();

        var storedEntityData = vial.getOrDefault(EIODataComponents.ENTITY_DATA, StoredEntityData.EMPTY);
        result.set(EIODataComponents.ENTITY_DATA, storedEntityData);

        return results;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.getDefaultInstance()), OutputStack.of(EIOItems.EMPTY_SOUL_VIAL.get().getDefaultInstance()));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(SoulBindingRecipe.Container container, Level pLevel) {
        if (!container.getItem(0).is(EIOItems.FILLED_SOUL_VIAL.get())) {
            return false;
        }

        if (!input.test(SoulBinderBlockEntity.INPUT_OTHER.getItemStack(container))) {
            return false;
        }

        if (!container.getItem(0).has(EIODataComponents.ENTITY_DATA)) {
            return false;
        }

        var storedEntityData = container.getItem(0).get(EIODataComponents.ENTITY_DATA);
        if (storedEntityData.entityType().isEmpty()) {
            return false;
        }

        var storedEntityType = storedEntityData.entityType().get();

        if (soulData.isPresent()) { //is in the selected souldata
            if (SoulDataReloadListener.fromString(soulData.get()).matches(
                storedEntityData.entityType().get()).isEmpty()) {
                return false;
            }

            return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= experience;
        }

        if (mobCategory.isPresent()) {
            // TODO: We can just call get(...) if we don't care about registry defaulting.
            var entityTypeOptional = BuiltInRegistries.ENTITY_TYPE.getOptional(storedEntityType);
            if (entityTypeOptional.isEmpty()) {
                return false;
            }

            var entityType = entityTypeOptional.get();

            if (!entityType.getCategory().equals(mobCategory.get())) {
                return false;
            }
        }

        if (entityType.isPresent()) {
            if (!storedEntityType.equals(entityType.get())) {
                return false;
            }
        }

        return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= experience;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.SOUL_BINDING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.SOUL_BINDING.type().get();
    }

    public static class Container extends RecipeWrapper {

        private final Supplier<Integer> fluid;

        public Container(IItemHandlerModifiable inv, Supplier<Integer> fluid) {
            super(inv);
            this.fluid = fluid;
        }
    }

    public static class Serializer implements RecipeSerializer<SoulBindingRecipe> {

        private static final MapCodec<SoulBindingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(SoulBindingRecipe::output),
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SoulBindingRecipe::input),
            Codec.INT.fieldOf("energy").forGetter(SoulBindingRecipe::energy),
            Codec.INT.fieldOf("exp").forGetter(SoulBindingRecipe::experience),
            ResourceLocation.CODEC.optionalFieldOf("entity_type").forGetter(SoulBindingRecipe::entityType),
            MobCategory.CODEC.optionalFieldOf("mob_category").forGetter(SoulBindingRecipe::mobCategory),
            Codec.STRING.optionalFieldOf("souldata").forGetter(SoulBindingRecipe::soulData)
        ).apply(instance, SoulBindingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SoulBindingRecipe> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.registry(Registries.ITEM),
            SoulBindingRecipe::output,
            Ingredient.CONTENTS_STREAM_CODEC,
            SoulBindingRecipe::input,
            ByteBufCodecs.INT,
            SoulBindingRecipe::energy,
            ByteBufCodecs.INT,
            SoulBindingRecipe::experience,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional),
            SoulBindingRecipe::entityType,
            // TODO: This is a bit gross, could do better.
            ByteBufCodecs.STRING_UTF8
                .map(MobCategory::byName, MobCategory::getName)
                .apply(ByteBufCodecs::optional),
            SoulBindingRecipe::mobCategory,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional),
            SoulBindingRecipe::soulData,
            SoulBindingRecipe::new
        );

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
