package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.recipe.RecipeDataUtil;
import com.enderio.core.common.util.JsonUtil;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.souldata.EngineSoul;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SoulBindingRecipeProvider extends EnderRecipeProvider {

    public SoulBindingRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        build(EIOItems.ENTICING_CRYSTAL, Ingredient.of(Tags.Items.GEMS_EMERALD), 51200, 4, EntityType.VILLAGER, pFinishedRecipeConsumer);
        build(EIOItems.ENDER_CRYSTAL, Ingredient.of(EIOTags.Items.GEMS_VIBRANT_CRYSTAL), 76800, 6, EntityType.ENDERMAN, pFinishedRecipeConsumer);
        build(EIOItems.PRESCIENT_CRYSTAL, Ingredient.of(EIOTags.Items.GEMS_VIBRANT_CRYSTAL), 100000, 8, EntityType.SHULKER, pFinishedRecipeConsumer);
        build(EIOItems.FRANK_N_ZOMBIE, Ingredient.of(EIOItems.Z_LOGIC_CONTROLLER), 51200, 4, EntityType.ZOMBIE, pFinishedRecipeConsumer);
        build(EIOItems.SENTIENT_ENDER, Ingredient.of(EIOItems.ENDER_RESONATOR), 51200, 4, EntityType.WITCH, pFinishedRecipeConsumer);
        build(EIOItems.BROKEN_SPAWNER, Ingredient.of(EIOItems.BROKEN_SPAWNER), 288000, 8, pFinishedRecipeConsumer);
        build(MachineBlocks.POWERED_SPAWNER, Ingredient.of(MachineBlocks.POWERED_SPAWNER), 288000, 8, pFinishedRecipeConsumer);
        build(MachineBlocks.SOUL_ENGINE, Ingredient.of(MachineBlocks.SOUL_ENGINE), 188000, 5, EngineSoul.NAME, pFinishedRecipeConsumer);
        build(EIOItems.PLAYER_TOKEN, Ingredient.of(EIOItems.DARK_STEEL_BALL), 12800, 1, EntityType.VILLAGER, pFinishedRecipeConsumer);
        build(EIOItems.MONSTER_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 12800, 1, MobCategory.MONSTER, pFinishedRecipeConsumer);
        build(EIOItems.ANIMAL_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 12800, 1, MobCategory.CREATURE, pFinishedRecipeConsumer);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, EntityType<? extends Entity> entityType, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output.asItem()).getPath()),
            output.asItem().getDefaultInstance(), input, energy, exp, ForgeRegistries.ENTITY_TYPES.getKey(entityType), null, null));
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, MobCategory mobCategory, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output.asItem()).getPath()),
            output.asItem().getDefaultInstance(), input, energy, exp, null, mobCategory, null));
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, String souldata, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output.asItem()).getPath()),
            output.asItem().getDefaultInstance(), input, energy, exp, null, null, souldata));
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output.asItem()).getPath()),
            output.asItem().getDefaultInstance(), input, energy, exp, null, null, null));
    }

    protected static class FinishedSoulBindingRecipe extends EnderFinishedRecipe {

        private final ItemStack output;
        private final Ingredient input;
        private final int energy;
        private final int exp;
        @Nullable
        private final ResourceLocation entityType;
        @Nullable
        private final MobCategory mobCategory;
        @Nullable
        private final String souldata;

        public FinishedSoulBindingRecipe(ResourceLocation id, ItemStack output, Ingredient input, int energy, int exp, @Nullable ResourceLocation entityType, @Nullable MobCategory mobCategory, @Nullable String souldata) {
            super(id);
            this.output = output;
            this.input = input;
            this.energy = energy;
            this.exp = exp;

            if (entityType != null && mobCategory != null) {
                throw new IllegalStateException("entityType and mobCategory are mutually exclusive!");
            }

            if (souldata != null && mobCategory != null) {
                throw new IllegalStateException("souldata and mobCategory are mutually exclusive!");
            }

            if (entityType != null && souldata != null) {
                throw new IllegalStateException("entityType and souldata are mutually exclusive!");
            }

            this.entityType = entityType;
            this.mobCategory = mobCategory;
            this.souldata = souldata;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("output", JsonUtil.serializeItemStackWithoutNBT(output));
            json.add("input", input.toJson());

            json.addProperty("energy", energy);
            json.addProperty("exp", exp);

            if (entityType != null) {
                json.addProperty("entity_type", entityType.toString());
            }

            if (mobCategory != null) {
                json.addProperty("mob_category", mobCategory.getName());
            }

            if (souldata != null) {
                json.addProperty("souldata", souldata);
            }

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>(RecipeDataUtil.getIngredientModIds(input));
            mods.add(ForgeRegistries.ITEMS.getKey(output.getItem()).getNamespace());
            return mods;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.SOUL_BINDING.serializer().get();
        }
    }
}
