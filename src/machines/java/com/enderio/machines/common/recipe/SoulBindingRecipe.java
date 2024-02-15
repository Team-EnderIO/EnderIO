package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.api.capability.IEntityStorage;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.SoulBinderBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.souldata.SoulDataReloadListener;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SoulBindingRecipe implements MachineRecipe<SoulBindingRecipe.Container> {

    final Item output;
    final Ingredient input;
    final int energy;
    final int exp;


    @Nullable
    final ResourceLocation entityType;
    @Nullable
    final MobCategory mobCategory;
    @Nullable
    final String souldata;

    public SoulBindingRecipe(Item output, Ingredient input, int energy, int exp, Optional<ResourceLocation> entityType,
        Optional<MobCategory> mobCategory, Optional<String> souldata) {
        this.output = output;
        this.input = input;
        this.energy = energy;
        this.exp = exp;



        if (Stream.of(entityType, mobCategory, souldata).filter(Optional::isPresent).count() > 1) {
            throw new IllegalStateException("entityType, mobCategory and souldata are mutually exclusive! You can only set one");
        }
        this.entityType = entityType.orElse(null);
        this.mobCategory = mobCategory.orElse(null);
        this.souldata = souldata.orElse(null);
    }

    public Ingredient getInput() {
        return input;
    }

    @Nullable
    public ResourceLocation getEntityType() {
        return entityType;
    }

    @Nullable
    public MobCategory getMobCategory() {
        return mobCategory;
    }

    @Nullable
    public String getSouldata() {
        return souldata;
    }

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    public int getExpCost() {
        return exp;
    }

    @Override
    public List<OutputStack> craft(SoulBindingRecipe.Container container, RegistryAccess registryAccess) {
        ItemStack vial = SoulBinderBlockEntity.INPUT_SOUL.getItemStack(container);
        List<OutputStack> results = getResultStacks(registryAccess);
        ItemStack result = results.get(0).getItem();
        vial.getCapability(EIOCapabilities.ENTITY_STORAGE)
            .ifPresent(inputEntity -> result.getCapability(EIOCapabilities.ENTITY_STORAGE)
                .ifPresent(resultEntity -> resultEntity.setStoredEntityData(inputEntity.getStoredEntityData())));
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

        LazyOptional<IEntityStorage> capability = container.getItem(0).getCapability(EIOCapabilities.ENTITY_STORAGE);
        if (!capability.isPresent()) { //vial (or other entity storage)
            return false;
        }

        if (souldata != null) { //is in the selected souldata
            if (SoulDataReloadListener.fromString(souldata).matches(
                container.getItem(0).getCapability(EIOCapabilities.ENTITY_STORAGE).resolve().get()
                    .getStoredEntityData().getEntityType().get()).isEmpty()) {
                return false;
            }

            return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= exp;
        }

        if (mobCategory == null && entityType == null) { //No souldata, entity type or mob category
            return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= exp;
        }

        IEntityStorage storage = capability.resolve().get();
        if (storage.hasStoredEntity()) {
            var type = storage.getStoredEntityData().getEntityType();
            if (type.isEmpty()) {
                return false;
            }

            var entityType = BuiltInRegistries.ENTITY_TYPE.get(type.get());
            if (entityType == null) {
                return false;
            }

            if (entityType.getCategory().equals(mobCategory)) {
                return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= exp;
            }
        }
        //type matters
        if (storage.hasStoredEntity() && storage.getStoredEntityData().getEntityType().get().equals(entityType)) {
            return ExperienceUtil.getLevelFromFluid(container.fluid.get()) >= exp;
        }

        return false;
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

        private static final Codec<SoulBindingRecipe> CODEC = RecordCodecBuilder.<SoulBindingRecipe>create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(recipe -> recipe.output),
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
            Codec.INT.fieldOf("energy").forGetter(recipe -> recipe.energy),
            Codec.INT.fieldOf("exp").forGetter(recipe -> recipe.exp),
            ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "entity_type").forGetter(recipe -> Optional.ofNullable(recipe.entityType)),
            ExtraCodecs.strictOptionalField(MobCategory.CODEC, "mob_category").forGetter(recipe -> Optional.ofNullable(recipe.mobCategory)),
            ExtraCodecs.strictOptionalField(Codec.STRING, "souldata").forGetter(recipe -> Optional.ofNullable(recipe.souldata))
        ).apply(instance, SoulBindingRecipe::new));

        @Override
        public Codec<SoulBindingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @Nullable SoulBindingRecipe fromNetwork(FriendlyByteBuf buffer) {
            try {
                ResourceLocation outputId = buffer.readResourceLocation();
                Item output = BuiltInRegistries.ITEM.get(outputId);
                if (output == Items.AIR) {
                    return null;
                }
                Ingredient input = Ingredient.fromNetwork(buffer);
                int energy = buffer.readInt();
                int exp = buffer.readInt();
                int mode = buffer.readInt();

                ResourceLocation entityType = null;
                if (mode == 1) {
                    entityType = buffer.readResourceLocation();
                }

                MobCategory mobCategory = null;
                if (mode == 2) {
                    mobCategory = MobCategory.byName(buffer.readUtf());
                }

                String souldata = null;
                if (mode == 3) {
                    souldata = buffer.readUtf();
                }

                return new SoulBindingRecipe(output, input, energy, exp, Optional.ofNullable(entityType), Optional.ofNullable(mobCategory), Optional.ofNullable(souldata));
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading soul binding recipe from packet.", ex);
                return null;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SoulBindingRecipe recipe) {
            try {
                buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.output));
                recipe.input.toNetwork(buffer);
                buffer.writeInt(recipe.energy);
                buffer.writeInt(recipe.exp);

                if (recipe.entityType != null) { //don't write null
                    buffer.writeInt(1);
                    buffer.writeResourceLocation(recipe.entityType);
                } else if (recipe.mobCategory != null) { //don't write null
                    buffer.writeInt(2);
                    buffer.writeUtf(recipe.mobCategory.getName());
                } else if (recipe.souldata != null) { //don't write null
                    buffer.writeInt(3);
                    buffer.writeUtf(recipe.souldata);
                } else {
                    buffer.writeInt(0);
                }
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing soul binding recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
