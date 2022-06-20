package com.enderio.machines.data.recipes;

import com.enderio.base.common.init.EIOEnchantments;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.data.recipe.EnderRecipeProvider;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EnchanterRecipeProvider extends EnderRecipeProvider {
    public EnchanterRecipeProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        //vanilla
        build(Enchantments.ALL_DAMAGE_PROTECTION, EIOItems.DARK_STEEL_INGOT.get(), 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.FIRE_PROTECTION, Items.BLAZE_POWDER, 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.FALL_PROTECTION, Tags.Items.FEATHERS, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.BLAST_PROTECTION, Items.GUNPOWDER, 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.PROJECTILE_PROTECTION, Tags.Items.LEATHER, 16, 1, pFinishedRecipeConsumer);//change arrow->leather?
        build(Enchantments.RESPIRATION, Items.GLASS_BOTTLE, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.AQUA_AFFINITY, Items.LILY_PAD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.DEPTH_STRIDER, Items.PRISMARINE_SHARD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FROST_WALKER, Items.ICE, 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.THORNS, Items.ROSE_BUSH, 4, 1, pFinishedRecipeConsumer);
        build(Enchantments.SHARPNESS, Tags.Items.GEMS_QUARTZ, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.SMITE, Items.ROTTEN_FLESH, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.BANE_OF_ARTHROPODS, Items.SPIDER_EYE, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.KNOCKBACK, Items.PISTON, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FIRE_ASPECT, Items.BLAZE_ROD, 8, 1, pFinishedRecipeConsumer);
        build(Enchantments.MOB_LOOTING, Items.SKELETON_SKULL, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.BLOCK_EFFICIENCY, Tags.Items.DUSTS_REDSTONE, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.SILK_TOUCH, Tags.Items.SLIMEBALLS, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.UNBREAKING, Tags.Items.OBSIDIAN, 1, 1, pFinishedRecipeConsumer);
        //build(Enchantments.MENDING, EIOItems.xp, 12, 1, pFinishedRecipeConsumer); //TODO "enderio:item_xp_transfer"
        build(Enchantments.BLOCK_FORTUNE, Tags.Items.GEMS_EMERALD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.POWER_ARROWS, Items.FLINT, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.PUNCH_ARROWS, Tags.Items.STRING, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FLAMING_ARROWS, Tags.Items.NETHERRACK, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.INFINITY_ARROWS, EIOItems.GRAINS_OF_INFINITY.get(), 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FISHING_LUCK, Tags.Items.GEMS_LAPIS, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FISHING_SPEED, ItemTags.FISHES, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.SWEEPING_EDGE, Tags.Items.INGOTS_IRON, 8, 1, pFinishedRecipeConsumer);
        //new
        build(Enchantments.CHANNELING, Items.LIGHTNING_ROD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.IMPALING, Tags.Items.INGOTS_IRON, 8, 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.LOYALTY, Items.LEAD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.MULTISHOT, ItemTags.ARROWS, 16, 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.PIERCING, Tags.Items.INGOTS_IRON, 8, 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.QUICK_CHARGE, Items.SUGAR, 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.RIPTIDE, Tags.Items.INGOTS_IRON, 8, 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.SOUL_SPEED, Items.SOUL_SOIL, 16, 1, pFinishedRecipeConsumer);


        //enderio
        build(EIOEnchantments.SOULBOUND.get(), EIOItems.ENDER_CRYSTAL.get(), 1, 1, pFinishedRecipeConsumer);
        //build(EIOEnchantments.WITHERING_ARROW.get(), witherpotion, 1, 1, pFinishedRecipeConsumer); //TODO Potion:"enderio:withering"
        //build(EIOEnchantments.WITHERING_BOLT.get(), witherpotion, 1, 1, pFinishedRecipeConsumer); //TODO Another recipe for bolts
        build(EIOEnchantments.WITHERING_BLADE.get(), EIOItems.WITHERING_POWDER.get(), 4, 1, pFinishedRecipeConsumer);
        build(EIOEnchantments.REPELLENT.get(), Items.ENDER_PEARL, 4, 2, pFinishedRecipeConsumer);
    }

    protected void build(Enchantment enchantment, Item input, int amountPerLevel, int levelModifier, Consumer<FinishedRecipe> recipeConsumer) {
        build(enchantment, Ingredient.of(input), amountPerLevel, levelModifier, recipeConsumer);
    }
    
    protected void build(Enchantment enchantment, TagKey<Item> input, int amountPerLevel, int levelModifier, Consumer<FinishedRecipe> recipeConsumer) {
        build(enchantment, Ingredient.of(input), amountPerLevel, levelModifier, recipeConsumer);
    }

    protected void build(Enchantment enchantment, Ingredient input, int amountPerLevel, int levelModifier, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new FinishedEnchantingRecipe(EIOMachines.loc("enchanting/" + enchantment.getRegistryName().getPath()), enchantment, input, amountPerLevel, levelModifier));
    }

    protected static class FinishedEnchantingRecipe extends EnderFinishedRecipe {

        private final Enchantment enchantment;
        private final Ingredient input;
        private final int amountPerLevel;
        private final int costMultiplier;

        public FinishedEnchantingRecipe(ResourceLocation id, Enchantment enchantment, Ingredient input, int amountPerLevel, int costMultiplier) {
            super(id);
            this.enchantment = enchantment;
            this.input = input;
            this.amountPerLevel = amountPerLevel;
            this.costMultiplier = costMultiplier;
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>();
            Arrays.stream(input.getItems()).forEach(item -> mods.add(item.getItem().getRegistryName().getNamespace()));
            mods.add(enchantment.getRegistryName().getNamespace());
            return mods;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("enchantment", enchantment.getRegistryName().toString());
            json.add("input", input.toJson());
            json.addProperty("amount", amountPerLevel);
            json.addProperty("cost_multiplier", costMultiplier);
            super.serializeRecipeData(json);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.Serializer.ENCHANTING.get();
        }

    }
}